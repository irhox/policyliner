package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.dto.CreatePolicyDTO;
import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.PolicyDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import de.tub.dima.policyliner.services.PolicyService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

    @POST
    @Path("/analyze")
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyzePolicies() {
        Log.info("Starting manual policy evaluation");
        policyService.evaluateDisclosurePolicies();
        return Response.ok().build();
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public PagedResponseDTO<PolicyDTO> searchPolicies(SearchDTO searchDTO) {
        return policyService.searchPolicies(searchDTO);
    }

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPolicy(CreatePolicyDTO createPolicyDTO) {
        Log.info("Creating policy");
        PolicyDTO createdPolicy = policyService.createPolicy(createPolicyDTO);
        return Response.ok(createdPolicy).build();
    }


}
