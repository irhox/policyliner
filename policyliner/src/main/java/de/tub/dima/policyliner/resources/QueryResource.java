package de.tub.dima.policyliner.resources;

import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.QueryRequestDTO;
import de.tub.dima.policyliner.dto.QueryResponseDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import de.tub.dima.policyliner.services.QueryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/query")
public class QueryResource {

    private final QueryService queryService;

    @Inject
    public QueryResource(QueryService queryService) {
        this.queryService = queryService;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String helloPolicy() {
        return "Hello Query";
    }

    @POST
    @Path("/analyze")
    @Produces(MediaType.APPLICATION_JSON)
    public QueryResponseDTO analyzeQuery(QueryRequestDTO disclosureQueryDTO) {

        return queryService.analyzeQuery(disclosureQueryDTO);
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public PagedResponseDTO<QueryResponseDTO> searchQueries(SearchDTO searchDTO) {
        return queryService.searchQueries(searchDTO);
    }

    @GET
    @Path("/{queryId}")
    public QueryResponseDTO getDisclosureQueryById(@PathParam("queryId") String queryId) {
        return queryService.getQueryById(queryId);
    }
}
