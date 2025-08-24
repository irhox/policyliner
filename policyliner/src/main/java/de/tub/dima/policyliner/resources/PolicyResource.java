package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.dto.*;
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
    @Path("/create/object")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPolicyFromObject(CreatePolicyDTO createPolicyDTO) {
        Log.info("Creating policy from object");
        PolicyDTO createdPolicy = policyService.createPolicy(createPolicyDTO);
        return Response.ok(createdPolicy).build();
    }

    @POST
    @Path("/create/query-string")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPolicyFromQueryString(CreatePolicyFromStringDTO disclosurePolicyDTO) {
        CreatePolicyDTO createPolicyDTO = policyService.parseDisclosurePolicyStatement(disclosurePolicyDTO);
        Log.info("Creating policy from Mascara query string");
        PolicyDTO createdPolicy = policyService.createPolicy(createPolicyDTO);
        return Response.ok(createdPolicy).build();
    }


}
