package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.dto.AlertDTO;
import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import de.tub.dima.policyliner.services.AlertService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/alert")
public class AlertResource {

    private final AlertService alertService;

    @Inject
    public AlertResource(AlertService alertService) {
        this.alertService = alertService;
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public PagedResponseDTO<AlertDTO> searchAlerts(SearchDTO searchDTO) {
        return alertService.searchAlerts(searchDTO);
    }
}
