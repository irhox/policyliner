package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.database.Alert;
import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.PolicyDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PolicyService {

    @Scheduled(every = "{policy.evaluation.interval}")
    public void evaluateDisclosurePolicies() {
        System.out.println("Evaluating Disclosure Policies");
        Alert history = new Alert();
        history.message = "Policy Evaluation Started";
    }

    public PagedResponseDTO<PolicyDTO> searchPolicies(SearchDTO searchDTO, String policyStatus) {
        return null;
    }
}
