package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.QueryRequestDTO;
import de.tub.dima.policyliner.dto.QueryResponseDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QueryService {

    public QueryResponseDTO analyzeQuery(QueryRequestDTO disclosureQueryDTO) {
        return null;
    }

    public PagedResponseDTO<QueryResponseDTO> searchQueries(SearchDTO searchDTO) {
        return null;
    }
}
