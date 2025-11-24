package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.constants.PolicyStatus;
import de.tub.dima.policyliner.database.data.DataDBService;
import de.tub.dima.policyliner.database.policyliner.Alert;
import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.database.policyliner.PolicyRepository;
import de.tub.dima.policyliner.dto.*;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifier;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifiers;
import de.tub.dima.policyliner.services.metrics.DeltaPresenceService;
import de.tub.dima.policyliner.services.metrics.PopulationUniquenessEstimationService;
import de.tub.dima.policyliner.services.metrics.SampleUniquenessRatioService;
import de.tub.dima.policyliner.services.metrics.TClosenessService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final DataDBService dataDBService;
    private final SampleUniquenessRatioService sampleUniquenessRatioService;
    private final DeltaPresenceService deltaPresenceService;
    private final PrivacyMetricValuesService privacyMetricValuesService;
    private final TClosenessService tclosenessService;
    private final PopulationUniquenessEstimationService populationUniquenessEstimationService;

    public PolicyService(
            PolicyRepository policyRepository,
            DataDBService dataDBService,
            SampleUniquenessRatioService sampleUniquenessRatioService,
            PrivacyMetricValuesService privacyMetricValuesService,
            DeltaPresenceService deltaPresenceService,
            TClosenessService tclosenessService,
            PopulationUniquenessEstimationService populationUniquenessEstimationService) {
        this.policyRepository = policyRepository;
        this.dataDBService = dataDBService;
        this.sampleUniquenessRatioService = sampleUniquenessRatioService;
        this.privacyMetricValuesService = privacyMetricValuesService;
        this.deltaPresenceService = deltaPresenceService;
        this.tclosenessService = tclosenessService;
        this.populationUniquenessEstimationService = populationUniquenessEstimationService;
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
        List<Policy> activePolicies = policyRepository.findByStatus(PolicyStatus.ACTIVE).stream().toList();
        for (Policy currentPolicy : activePolicies) {
            sampleUniquenessRatioService.evaluatePolicyAgainstMetric(currentPolicy, null);
            deltaPresenceService.evaluatePolicyAgainstMetric(currentPolicy, null);
            tclosenessService.evaluatePolicyAgainstMetric(currentPolicy, null);
            populationUniquenessEstimationService.evaluatePolicyAgainstMetric(currentPolicy, null);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void evaluateDisclosurePolicy(String policyId, JsonQuasiIdentifier quasiIdentifier) {
        Policy policy = policyRepository.findById(policyId);
        if (policy == null) throw new RuntimeException("No policy with id " + policyId);
        sampleUniquenessRatioService.evaluatePolicyAgainstMetric(policy, new JsonQuasiIdentifiers(List.of(quasiIdentifier)));
        deltaPresenceService.evaluatePolicyAgainstMetric(policy, new JsonQuasiIdentifiers(List.of(quasiIdentifier)));
        tclosenessService.evaluatePolicyAgainstMetric(policy, new JsonQuasiIdentifiers(List.of(quasiIdentifier)));
        populationUniquenessEstimationService.evaluatePolicyAgainstMetric(policy, new JsonQuasiIdentifiers(List.of(quasiIdentifier)));
    }

    public PolicyDTO getPolicyById(String policyId) {
        return convertToPolicyDTO(policyRepository.findById(policyId));
    }

    // TODO: Implement sorting
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

    @Transactional
    public PolicyDTO deactivatePolicy(String policyId) {
        Policy policy = policyRepository.findById(policyId);
        if (policy == null) throw new RuntimeException("No policy with id " + policyId);

        if (policy.status == PolicyStatus.ACTIVE) {
            policy.status = PolicyStatus.INACTIVE;
            policy.deactivatedAt = LocalDateTime.now();
        }
        Policy.getEntityManager().merge(policy);
        return convertToPolicyDTO(policy);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public PolicyDTO createPolicy(CreatePolicyDTO createPolicyDTO) {
        String policy = dataDBService.createPolicy(createPolicyDTO);
        Policy newPolicy = new Policy();
        if (createPolicyDTO.getUseStaticMasking()) {
            newPolicy.materializedViewName = createPolicyDTO.getPolicyName();
        } else {
            newPolicy.viewName = createPolicyDTO.getPolicyName();
        }
        newPolicy.policy = policy;
        newPolicy.status = PolicyStatus.ACTIVE;
        newPolicy.allowedUserRole = createPolicyDTO.getUserRole();
        policyRepository.persist(newPolicy);
        if (createPolicyDTO.getUseDefaultMetrics()) {
            privacyMetricValuesService.storeAllDefaultPrivacyMetricValuesForPolicy(newPolicy);
        }
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
        disclosurePolicyInfo.setUseStaticMasking(disclosurePolicyDTO.getUseStaticMasking());
        disclosurePolicyInfo.setUseDefaultMetrics(disclosurePolicyDTO.getUseDefaultMetrics());
        disclosurePolicyInfo.setEvaluatePolicyUponCreation(disclosurePolicyDTO.getEvaluatePolicyUponCreation());
        disclosurePolicyInfo.setQuasiIdentifier(disclosurePolicyDTO.getQuasiIdentifier());

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
        if (disclosurePolicyInfo.getQuasiIdentifier() != null && disclosurePolicyInfo.getQuasiIdentifier().getViewName() != null) {
            disclosurePolicyInfo.setPolicyName(disclosurePolicyInfo.getQuasiIdentifier().getViewName());
        } else {
            disclosurePolicyInfo.setPolicyName(String.join("_", tableNames) + "_policy");
        }

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
                policy.alerts != null ?
                        policy.alerts.stream().filter(a -> !a.isResolved).collect(Collectors.toMap(Alert::getId, a -> a.severity))
                        : Map.of());
    }
}
