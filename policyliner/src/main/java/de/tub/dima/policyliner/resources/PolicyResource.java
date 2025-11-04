package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.dto.*;
import de.tub.dima.policyliner.services.PolicyService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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

    @GET
    @Path("/{policyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PolicyDTO getPolicyById(@PathParam("policyId") String policyId) {
        return policyService.getPolicyById(policyId);
    }

    @POST
    @Path("/analyze")
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyzePolicies() {
        Log.info("Starting manual policy evaluation");
        policyService.evaluateDisclosurePolicies();
        return Response.ok().build();
    }

    @POST
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
        if (createPolicyDTO.getEvaluatePolicyUponCreation()) {
            policyService.evaluateDisclosurePolicy(createdPolicy.getId(), createPolicyDTO.getQuasiIdentifier());
        }
        return Response.ok(createdPolicy).build();
    }

    @POST
    @Path("/create/query-string")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPolicyFromQueryString(CreatePolicyFromStringDTO disclosurePolicyDTO) {
        CreatePolicyDTO createPolicyDTO = policyService.parseDisclosurePolicyStatement(disclosurePolicyDTO);
        Log.info("Creating policy from Mascara query string");
        PolicyDTO createdPolicy = policyService.createPolicy(createPolicyDTO);
        if (disclosurePolicyDTO.getEvaluatePolicyUponCreation()) {
            policyService.evaluateDisclosurePolicy(createdPolicy.getId(), disclosurePolicyDTO.getQuasiIdentifier());
        }
        return Response.ok(createdPolicy).build();
    }

    @PUT
    @Path("/deactivate/{policyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PolicyDTO deactivatePolicy(@PathParam("policyId") String policyId) {
        return policyService.deactivatePolicy(policyId);
    }


}
