package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.dto.PrivacyMetricDTO;
import de.tub.dima.policyliner.services.PrivacyMetricValuesService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/privacy-metric")
public class PrivacyMetricResource {
    private final PrivacyMetricValuesService privacyMetricService;

    @Inject
    public PrivacyMetricResource(PrivacyMetricValuesService privacyMetricService) {
        this.privacyMetricService = privacyMetricService;
    }

    @GET
    @Path("/of-policy/{policyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PrivacyMetricDTO> getPrivacyMetricsOfPolicy(@PathParam("policyId") String policyId){
        return privacyMetricService.getPrivacyMetricValuesByPolicyId(policyId);
    }

    @GET
    @Path("/{metricId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PrivacyMetricDTO getPrivacyMetricById(@PathParam("metricId") String metricId){
        return privacyMetricService.getPrivacyMetricValueById(metricId);
    }

    @PUT
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public PrivacyMetricDTO createOrUpdatePrivacyMetric(PrivacyMetricDTO privacyMetric){
        return privacyMetricService.createPrivacyMetricValue(privacyMetric);
    }

    @DELETE
    @Path("/{metricId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePrivacyMetric(@PathParam("metricId") String metricId){
        boolean deletedMetric = privacyMetricService.deletePrivacyMetricValue(metricId);

        if(deletedMetric){
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}
