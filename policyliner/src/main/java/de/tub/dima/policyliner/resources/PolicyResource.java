package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.PolicyDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import de.tub.dima.policyliner.services.PolicyService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/policy")
public class PolicyResource {


    private final PolicyService policyService;

    @Inject
    public PolicyResource(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String helloPolicy() {
        return "Hello Policy";
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public PagedResponseDTO<PolicyDTO> searchPolicies(SearchDTO searchDTO, String policyStatus) {
        return policyService.searchPolicies(searchDTO, policyStatus);
    }


}
