package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;
import de.tub.dima.policyliner.constants.MetricSeverity;
import de.tub.dima.policyliner.constants.PolicyStatus;
import de.tub.dima.policyliner.database.data.DataDBService;
import de.tub.dima.policyliner.database.policyliner.*;
import de.tub.dima.policyliner.dto.*;
import de.tub.dima.policyliner.entities.SampleUniquenessReport;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final DataDBService dataDBService;
    private final UniquenessEstimationService uniquenessEstimationService;
    private final PrivacyMetricRepository privacyMetricRepository;

    public PolicyService(
            PolicyRepository policyRepository,
            DataDBService dataDBService,
            UniquenessEstimationService uniquenessEstimationService,
            PrivacyMetricRepository privacyMetricRepository) {
        this.policyRepository = policyRepository;
        this.dataDBService = dataDBService;
        this.uniquenessEstimationService = uniquenessEstimationService;
        this.privacyMetricRepository = privacyMetricRepository;
    }

    @Scheduled(every = "{policy.evaluation.interval}")
    @RunOnVirtualThread
    @Transactional(Transactional.TxType.REQUIRED)
    public void evaluateDisclosurePoliciesCronJob() {
        Log.info("Evaluating Disclosure Policies");
        Instant start = Instant.now();
        evaluateDisclosurePolicies();
        Instant end = Instant.now();
        Log.info("Finished evaluating Disclosure Policies in " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void evaluateDisclosurePolicies() {
        List<String> activePolicyViewNames = policyRepository.findByStatus(PolicyStatus.ACTIVE).stream().map(p -> p.viewName).toList();
        List<SampleUniquenessReport> sampleUniquenessOfTables = uniquenessEstimationService.computeMetricForTables(activePolicyViewNames);
        for (SampleUniquenessReport report : sampleUniquenessOfTables) {
            Policy currentPolicy = policyRepository.findByViewName(report.getViewName()).stream().findFirst().orElse(null);
            if (currentPolicy == null) continue;
            List<PrivacyMetric> policyPrivacyMetrics = privacyMetricRepository.findByPolicyId(currentPolicy.getId());
            PrivacyMetric lowerUniquenessRatio = policyPrivacyMetrics.stream().
                    filter(p -> p.name.equals("uniquenessRatio") && p.metricSeverity.equals(MetricSeverity.LOWER_LIMIT))
                    .findFirst().orElse(null);
            PrivacyMetric upperUniquenessRatio = policyPrivacyMetrics.stream()
                    .filter(p -> p.name.equals("uniquenessRatio") && p.metricSeverity.equals(MetricSeverity.UPPER_LIMIT))
                    .findFirst().orElse(null);
            PrivacyMetric middleUniquenessRatio = policyPrivacyMetrics.stream()
                    .filter(p -> p.name.equals("uniquenessRatio") && p.metricSeverity.equals(MetricSeverity.MIDDLE_VALUE))
                    .findFirst().orElse(null);
            if (upperUniquenessRatio != null && report.getUniquenessRatio().doubleValue() > Double.parseDouble(upperUniquenessRatio.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.SEVERE;
                newAlert.message = """ 
                        Policy with view %s has a high uniqueness ratio of %.3f.
                        This is a cause for concern and should be reviewed. The view columns should most likely be generalized.
                        """.formatted(report.getViewName(), report.getUniquenessRatio());
                newAlert.persist();
                currentPolicy.alerts.add(newAlert);
            } else if (middleUniquenessRatio != null && report.getUniquenessRatio().doubleValue() > Double.parseDouble(middleUniquenessRatio.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.WARNING;
                newAlert.message = """ 
                        Policy with view %s has a high uniqueness ratio of %.3f.
                        Depending on the sensitivity of the data, this may be a cause for concern and should be reviewed.
                        """.formatted(report.getViewName(), report.getUniquenessRatio());
                newAlert.persist();
                currentPolicy.alerts.add(newAlert);
            } else if (lowerUniquenessRatio != null && report.getUniquenessRatio().doubleValue() < Double.parseDouble(lowerUniquenessRatio.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.INFO;
                newAlert.message = """ 
                        Policy with view %s has a low uniqueness ratio of %.3f.
                        Please review the generalization / masking of the data in this view.
                        The usability of the data can most likely be improved.
                """.formatted(report.getViewName(), report.getUniquenessRatio());
                newAlert.persist();
                currentPolicy.alerts.add(newAlert);
            }
            Policy.getEntityManager().merge(currentPolicy);
        }
    }

    public PolicyDTO getPolicyById(String policyId) {
        return convertToPolicyDTO(policyRepository.findById(policyId));
    }

    // TODO: Implement sorting and filtering
    public PagedResponseDTO<PolicyDTO> searchPolicies(SearchDTO searchDTO) {
        PanacheQuery<Policy> policyQuery;
        if (Objects.equals(searchDTO.getBooleanFilter(), PolicyStatus.ACTIVE.name()) ||
                Objects.equals(searchDTO.getBooleanFilter(), PolicyStatus.INACTIVE.name())) {
            policyQuery = policyRepository.findFilteredPoliciesByStatus(searchDTO.getFilter(), searchDTO.getBooleanFilter());
        } else {
            policyQuery = policyRepository.findFilteredPolicies(searchDTO.getFilter());
        }
        List<PolicyDTO> policyList = policyQuery.page(
                Page.of(
                        searchDTO.getPageNumber(),
                        searchDTO.getPageSize())
                ).list()
                .stream()
                .map(this::convertToPolicyDTO)
                .toList();

        return createPagedResponseDTO(policyList, searchDTO, policyQuery.count());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public PolicyDTO createPolicy(CreatePolicyDTO createPolicyDTO) {
        String policy = dataDBService.createPolicy(createPolicyDTO);
        Policy newPolicy = new Policy();
        if (createPolicyDTO.getIsMaterializedView()) {
            newPolicy.materializedViewName = createPolicyDTO.getPolicyName();
        } else {
            newPolicy.viewName = createPolicyDTO.getPolicyName();
        }
        newPolicy.policy = policy;
        newPolicy.status = PolicyStatus.ACTIVE;
        newPolicy.allowedUserRole = createPolicyDTO.getUserRole();
        policyRepository.persist(newPolicy);
        return convertToPolicyDTO(newPolicy);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void createMaterializedViewForExistingPolicy(Policy policy){
        Map<String, String> materializedViewMap = dataDBService.createMaterializedViewFromExistingPolicy(policy);
        if (materializedViewMap.isEmpty()) {
            throw new RuntimeException("Could not create materialized view for policy " + policy.viewName);
        }
        policy.policy = materializedViewMap.values().stream().findFirst().get();
        policy.materializedViewName = materializedViewMap.keySet().stream().findFirst().get();
        policyRepository.getEntityManager().merge(policy);
        convertToPolicyDTO(policy);
    }

    // Syntax of Disclosure Policy Creation Statement:
    // disclose attribute list from table [, ...]
    // [with mask on attribute using masking_function] [,..]
    // [where condition]
    public CreatePolicyDTO parseDisclosurePolicyStatement(CreatePolicyFromStringDTO disclosurePolicyDTO) {
        String disclosurePolicy = disclosurePolicyDTO.getPolicy();
        final CreatePolicyDTO disclosurePolicyInfo = new CreatePolicyDTO();
        disclosurePolicyInfo.setIsMaterializedView(disclosurePolicyDTO.getIsMaterializedView());

        final String attributes = disclosurePolicy.substring(disclosurePolicy.indexOf("disclose")+8, disclosurePolicy.indexOf("from"));
        int whereIndex = disclosurePolicy.contains("where") ? disclosurePolicy.indexOf("where") : disclosurePolicy.length();
        final String tables = disclosurePolicy.contains("with mask on")
                ? disclosurePolicy.substring(disclosurePolicy.indexOf("from")+4, disclosurePolicy.indexOf("with"))
                : disclosurePolicy.substring(disclosurePolicy.indexOf("from")+4, whereIndex);

        final String dataMasks = disclosurePolicy.substring(disclosurePolicy.indexOf("with mask on")+12, whereIndex);
        final String conditions = disclosurePolicy.substring(disclosurePolicy.indexOf("where")+5);
        List<String> conditionsList = Stream.of(conditions.split("and")).map(String::trim).toList();
        final Optional<String> userConditionOp = conditionsList.stream().filter(c -> c.contains("$user.role")).findFirst();
        if (userConditionOp.isPresent()) {
            conditionsList = conditionsList.stream().filter(c -> !c.contains("$user.role")).toList();
            disclosurePolicyInfo.setUserRole(
                    userConditionOp.get()
                            .substring(
                                    userConditionOp.get().indexOf("$user.role")+10)
                            .replaceAll("=", "")
                            .replaceAll("'", "")
                            .trim());
        }

        List<String> attributeList = Stream.of(attributes.split(",")).map(String::trim).toList();
        List<String> tableNames = Stream.of(tables.split(",")).map(String::trim).toList();
        if (tableNames.size() == 1) {
            TableInfoDTO tableInfo = new TableInfoDTO(tableNames.getFirst(), null, null);
            disclosurePolicyInfo.setTables(List.of(tableInfo));
        } else {
            List<String> finalConditionsList = conditionsList;
            List<TableInfoDTO> tableList = tableNames.stream().map(name -> {
                List<String> tableConditionsList = finalConditionsList.stream().filter(c -> c.contains(name)).toList();
                final TableInfoDTO tableInfo = new TableInfoDTO(name.trim(), null, null);
                tableConditionsList.forEach(condition -> {
                    int beginIndex = condition.indexOf(name + ".") + name.length()+1;
                    AtomicInteger endIndex = new AtomicInteger(condition.length());
                    if (condition.indexOf("=") > condition.indexOf(name + ".")) {
                        endIndex.set(condition.indexOf("="));
                        String primaryKey = condition.substring(beginIndex, endIndex.get()).trim();
                        tableInfo.setPrimaryKey(primaryKey);
                    } else {
                        String foreignKey = condition.substring(beginIndex, endIndex.get()).trim();
                        tableInfo.setForeignKey(foreignKey);
                    }

                });
                return tableInfo;
            }).toList();

            disclosurePolicyInfo.setTables(tableList);
        }

        List<ViewAttributeDTO> dataMaskObj = new ArrayList<>(Stream.of(dataMasks.split("with mask on")).map(mask -> {
            String attribute = mask.substring(0, mask.indexOf("using")).trim();
            String maskingFunction = mask.substring(mask.indexOf("using")+5, mask.lastIndexOf("("));
            List<String> arguments = Stream.of(mask.substring(mask.lastIndexOf("(")+1, mask.lastIndexOf(")")).split(",")).map(String::trim).toList();
            ViewAttributeDTO result = new ViewAttributeDTO();
            result.setFunctionName(maskingFunction);
            result.setTableColumnName(attribute);
            result.setViewColumnName(attribute.replace(".", "_").trim());
            if (!arguments.isEmpty() && !arguments.getFirst().isEmpty()) {
                result.setFunctionArguments(arguments);
            }
            return result;
        }).toList());

        attributeList.stream()
                .filter(a -> !dataMaskObj.stream().map(ViewAttributeDTO::getTableColumnName).toList().contains(a) &&
                        dataMaskObj.stream().filter(v -> v.getTableColumnName().contains(a)).toList().isEmpty()
                )
                .forEach(col -> {
                    ViewAttributeDTO viewAttributeDTO = new ViewAttributeDTO();
                    viewAttributeDTO.setTableColumnName(col.trim());
                    viewAttributeDTO.setViewColumnName(col.replace(".", "_").trim());
                    dataMaskObj.add(viewAttributeDTO);
                });

        disclosurePolicyInfo.setColumns(dataMaskObj);
        disclosurePolicyInfo.setPolicyName(String.join("_", tableNames) + "_policy");

        return disclosurePolicyInfo;
    }

    private PagedResponseDTO<PolicyDTO> createPagedResponseDTO(List<PolicyDTO> policies, SearchDTO searchDTO, Long totalElements) {
        PagedResponseDTO<PolicyDTO> page = new PagedResponseDTO<>();
        page.setCurrentPage(searchDTO.getPageNumber());
        page.setPageSize(searchDTO.getPageSize());
        page.setElements(policies);
        page.setTotalElements(totalElements);
        page.setTotalPages((int) (totalElements/searchDTO.getPageSize()));

        return page;
    }

    private PolicyDTO convertToPolicyDTO(Policy policy) {
        return new PolicyDTO(
                policy.getId(),
                policy.policy,
                policy.status,
                policy.createdAt,
                policy.allowedUserRole,
                policy.viewName,
                policy.materializedViewName,
                policy.deactivatedAt,
                policy.alerts.stream().filter(a -> !a.isResolved).collect(Collectors.toMap(Alert::getId, a -> a.severity)));
    }
}
