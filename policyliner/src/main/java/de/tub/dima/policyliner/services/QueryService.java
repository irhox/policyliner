package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.constants.QueryStatus;
import de.tub.dima.policyliner.database.policyliner.DisclosureQuery;
import de.tub.dima.policyliner.database.policyliner.DisclosureQueryRepository;
import de.tub.dima.policyliner.database.policyliner.UserRepository;
import de.tub.dima.policyliner.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class QueryService {

    private final DisclosureQueryRepository disclosureQueryRepository;
    private final UserRepository userRepository;

    public QueryService(DisclosureQueryRepository disclosureQueryRepository, UserRepository userRepository) {
        this.disclosureQueryRepository = disclosureQueryRepository;
        this.userRepository = userRepository;
    }

    // TODO: Work In Progress
    public QueryResponseDTO analyzeQuery(QueryRequestDTO disclosureQueryDTO) {
        Log.info("Evaluating query of user: " + disclosureQueryDTO.getUserId());
        List<DisclosureQuery> userQueries = disclosureQueryRepository.findByUserId(disclosureQueryDTO.getUserId());
        DisclosureQuery disclosureQuery = new DisclosureQuery();
        disclosureQuery.user = userRepository.findById(disclosureQueryDTO.getUserId());
        disclosureQuery.query = disclosureQueryDTO.getQuery();
        if (userQueries.isEmpty()) {
            Log.info("No previous queries by user: " + disclosureQueryDTO.getUserId() + " found");
            disclosureQuery.status = QueryStatus.APPROVED;
        } else {
            Log.info("Found " + userQueries.size() + " queries of user " + disclosureQueryDTO.getUserId());
        }
        disclosureQuery.persist();

        return convertToQueryResponseDTO(disclosureQuery);
    }

    // TODO: Implement sorting and filtering
    public PagedResponseDTO<QueryResponseDTO> searchQueries(SearchDTO searchDTO) {
        PanacheQuery<DisclosureQuery> disclosurePanacheQuery = disclosureQueryRepository.findAll();
        List<QueryResponseDTO> queryList = disclosurePanacheQuery.page(
                Page.of(searchDTO.getPageNumber(), searchDTO.getPageSize())
        ).list().stream().map(this::convertToQueryResponseDTO).toList();

        return createPagedResponseDTO(queryList, searchDTO);
    }

    private PagedResponseDTO<QueryResponseDTO> createPagedResponseDTO(List<QueryResponseDTO> queryList, SearchDTO searchDTO) {
        PagedResponseDTO<QueryResponseDTO> page = new PagedResponseDTO<>();
        page.setCurrentPage(searchDTO.getPageNumber());
        page.setPageSize(searchDTO.getPageSize());
        page.setElements(queryList);
        page.setTotalElements(queryList.size());
        page.setTotalPages(queryList.size()/searchDTO.getPageSize());

        return page;
    }

    private QueryResponseDTO convertToQueryResponseDTO(DisclosureQuery disclosureQuery) {
        return new QueryResponseDTO(disclosureQuery.getId(), disclosureQuery.query, disclosureQuery.status, disclosureQuery.message);
    }
}
