package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.QueryRequestDTO;
import de.tub.dima.policyliner.dto.QueryResponseDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import de.tub.dima.policyliner.services.QueryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/query")
public class QueryResource {

    private final QueryService queryService;

    @Inject
    public QueryResource(QueryService queryService) {
        this.queryService = queryService;
    }

    @POST
    @Path("/analyze")
    @Produces(MediaType.APPLICATION_JSON)
    public QueryResponseDTO analyzeQuery(QueryRequestDTO disclosureQueryDTO) {

        return queryService.analyzeQuery(disclosureQueryDTO);
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public PagedResponseDTO<QueryResponseDTO> searchQueries(SearchDTO searchDTO) {
        return queryService.searchQueries(searchDTO);
    }
}
