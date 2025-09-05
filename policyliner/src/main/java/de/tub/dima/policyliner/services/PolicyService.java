package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.constants.PolicyStatus;
import de.tub.dima.policyliner.database.data.DataDBService;
import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.database.policyliner.PolicyRepository;
import de.tub.dima.policyliner.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@ApplicationScoped
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final DataDBService dataDBService;

    public PolicyService(
            PolicyRepository policyRepository,
            DataDBService dataDBService) {
        this.policyRepository = policyRepository;
        this.dataDBService = dataDBService;
    }

    @Scheduled(every = "{policy.evaluation.interval}")
    public void evaluateDisclosurePoliciesCronJob() {
        evaluateDisclosurePolicies();
    }

    public void evaluateDisclosurePolicies() {
        Log.info("Evaluating Disclosure Policies");
        List<Policy> activePolicies = policyRepository.findByStatus(PolicyStatus.ACTIVE);
    }

    // TODO: Implement sorting and filtering
    public PagedResponseDTO<PolicyDTO> searchPolicies(SearchDTO searchDTO) {
        PanacheQuery<Policy> policyQuery;
        if (Objects.equals(searchDTO.getBooleanFilter(), PolicyStatus.ACTIVE.name()) ||
                Objects.equals(searchDTO.getBooleanFilter(), PolicyStatus.INACTIVE.name())) {
            policyQuery = policyRepository.find("status", searchDTO.getBooleanFilter());
        } else {
            policyQuery = policyRepository.findAll();
        }
        List<PolicyDTO> policyList = policyQuery.page(
                Page.of(
                        searchDTO.getPageNumber(),
                        searchDTO.getPageSize())
                ).list()
                .stream()
                .map(this::convertToPolicyDTO)
                .toList();

        return createPagedResponseDTO(policyList, searchDTO);
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
        final String tables = disclosurePolicy.contains("with mask on")
                ? disclosurePolicy.substring(disclosurePolicy.indexOf("from")+4, disclosurePolicy.indexOf("with"))
                : disclosurePolicy.substring(disclosurePolicy.indexOf("from")+4, disclosurePolicy.indexOf("where"));

        final String dataMasks = disclosurePolicy.substring(disclosurePolicy.indexOf("with mask on")+12, disclosurePolicy.indexOf("where"));
        final String conditions = disclosurePolicy.substring(disclosurePolicy.indexOf("where")+5);
        final List<String> conditionsList = Stream.of(conditions.split("and")).map(String::trim).toList();

        List<String> attributeList = Stream.of(attributes.split(",")).map(String::trim).toList();
        List<String> tableNames = Stream.of(tables.split(",")).map(String::trim).toList();
        if (tableNames.size() == 1) {
            TableInfoDTO tableInfo = new TableInfoDTO(tableNames.getFirst(), null, null);
            disclosurePolicyInfo.setTables(List.of(tableInfo));
        } else {
            List<TableInfoDTO> tableList = tableNames.stream().map(name -> {
                List<String> tableConditionsList = conditionsList.stream().filter(c -> c.contains(name)).toList();
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
            result.setViewColumnName(attribute);
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
                    viewAttributeDTO.setViewColumnName(col.trim());
                    dataMaskObj.add(viewAttributeDTO);
                });

        disclosurePolicyInfo.setColumns(dataMaskObj);
        disclosurePolicyInfo.setPolicyName(String.join("_", tableNames) + "_policy");

        return disclosurePolicyInfo;
    }

    private PagedResponseDTO<PolicyDTO> createPagedResponseDTO(List<PolicyDTO> policies, SearchDTO searchDTO) {
        PagedResponseDTO<PolicyDTO> page = new PagedResponseDTO<>();
        page.setCurrentPage(searchDTO.getPageNumber());
        page.setPageSize(searchDTO.getPageSize());
        page.setElements(policies);
        page.setTotalElements(policies.size());
        page.setTotalPages(policies.size()/searchDTO.getPageSize());

        return page;
    }

    private PolicyDTO convertToPolicyDTO(Policy policy) {
        return new PolicyDTO(policy.getId(), policy.policy, policy.status, policy.deactivatedAt);
    }
}
