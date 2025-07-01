package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.constants.PolicyStatus;
import de.tub.dima.policyliner.database.data.DataDBService;
import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.database.policyliner.PolicyRepository;
import de.tub.dima.policyliner.dto.CreatePolicyDTO;
import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.PolicyDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;

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
