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
import java.util.Objects;
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
        newPolicy.policy = policy;
        newPolicy.status = PolicyStatus.ACTIVE;
        policyRepository.persist(newPolicy);
        return convertToPolicyDTO(newPolicy);
    }

    // Syntax of Disclosure Policy String:
    // disclose attribute list from table [, ...]
    // [with mask on attribute using masking_function] [,..]
    // [where condition]
    public CreatePolicyDTO parseDisclosurePolicy(String disclosurePolicy) {
        final CreatePolicyDTO disclosurePolicyInfo = new CreatePolicyDTO();
        final String attributes = disclosurePolicy.substring(disclosurePolicy.indexOf("disclose")+8, disclosurePolicy.indexOf("from"));
        final String tables = disclosurePolicy.contains("with mask on")
                ? disclosurePolicy.substring(disclosurePolicy.indexOf("from")+4, disclosurePolicy.indexOf("with"))
                : disclosurePolicy.substring(disclosurePolicy.indexOf("from")+4, disclosurePolicy.indexOf("where"));

        String dataMasks = disclosurePolicy.substring(disclosurePolicy.indexOf("with mask on")+12, disclosurePolicy.indexOf("where"));


        List<String> attributeList = Stream.of(attributes.split(",")).map(String::trim).toList();
        List<String> tableList = Stream.of(tables.split(",")).map(String::trim).toList();
        List<ViewAttributeDTO> dataMaskObj = new ArrayList<>(Stream.of(dataMasks.split("with mask on")).map(mask -> {
            String attribute = mask.substring(0, mask.indexOf("using")).trim();
            String maskingFunction = mask.substring(mask.indexOf("using")+5, mask.indexOf("("));
            List<String> arguments = Stream.of(mask.substring(mask.indexOf("(")+1, mask.indexOf(")")).split(",")).map(String::trim).toList();
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
                .filter(a -> !dataMaskObj.stream().map(ViewAttributeDTO::getTableColumnName).toList().contains(a))
                .forEach(col -> {
                    ViewAttributeDTO viewAttributeDTO = new ViewAttributeDTO();
                    viewAttributeDTO.setTableColumnName(col.trim());
                    viewAttributeDTO.setViewColumnName(col.trim());
                    dataMaskObj.add(viewAttributeDTO);
                });

        disclosurePolicyInfo.setColumns(dataMaskObj);
        disclosurePolicyInfo.setTables(tableList);

        disclosurePolicyInfo.setPolicyName(String.join("_", tableList) + "_policy");

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
