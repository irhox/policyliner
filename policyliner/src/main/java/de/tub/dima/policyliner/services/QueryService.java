package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;
import de.tub.dima.policyliner.constants.QueryComparatorType;
import de.tub.dima.policyliner.constants.QueryStatus;
import de.tub.dima.policyliner.database.data.DataDBService;
import de.tub.dima.policyliner.database.policyliner.*;
import de.tub.dima.policyliner.dto.PagedResponseDTO;
import de.tub.dima.policyliner.dto.QueryRequestDTO;
import de.tub.dima.policyliner.dto.QueryResponseDTO;
import de.tub.dima.policyliner.dto.SearchDTO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class QueryService {

    private final DisclosureQueryRepository disclosureQueryRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final PolicyService policyService;
    private final DataDBService dataDBService;

    public QueryService(
            DisclosureQueryRepository disclosureQueryRepository,
            PolicyRepository policyRepository,
            UserRepository userRepository,
            PolicyService policyService,
            DataDBService dataDBService
    ) {
        this.disclosureQueryRepository = disclosureQueryRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.policyService = policyService;
        this.dataDBService = dataDBService;
    }

    // TODO: Work In Progress
    @Transactional(Transactional.TxType.REQUIRED)
    public QueryResponseDTO analyzeQuery(QueryRequestDTO disclosureQueryDTO) {
        Log.info("Evaluating query of user: " + disclosureQueryDTO.getUserId());
        DisclosureQuery disclosureQuery = new DisclosureQuery();


        // check if the user exists, if it doesn't, create the new user
        User queryUser = userRepository.findById(disclosureQueryDTO.getUserId());
        if (queryUser == null) {
            Log.info("User with id: " + disclosureQueryDTO.getUserId() + " not found");
            User newUser = new User();
            newUser.id = disclosureQueryDTO.getUserId();
            newUser.role = disclosureQueryDTO.getUserRole();
            newUser.persist();
            Log.info("Created new user with id: " + disclosureQueryDTO.getUserId());
            disclosureQuery.status = QueryStatus.APPROVED;
            disclosureQuery.query = disclosureQueryDTO.getQuery();
            disclosureQuery.user = newUser;
            disclosureQuery.persist();
            return convertToQueryResponseDTO(disclosureQuery);
        }
        List<DisclosureQuery> userQueries = disclosureQueryRepository.findByUserId(disclosureQueryDTO.getUserId());
        disclosureQuery.user = queryUser;
        disclosureQuery.query = disclosureQueryDTO.getQuery();
        if (userQueries.isEmpty()) {
            Log.info("No previous queries by user: " + disclosureQueryDTO.getUserId() + " found");
            disclosureQuery.status = QueryStatus.APPROVED;
        } else {
            Log.info("Found " + userQueries.size() + " queries of user " + disclosureQueryDTO.getUserId());
            // protect from data reconstruction attacks
            if (userQueries.size() > 1) {
                List<String> similarQueries = new ArrayList<>();
                userQueries.forEach(q -> {
                    if (disclosureQueryDTO.getComparatorType() == QueryComparatorType.CUSTOM) {
                        Double queryDifference = checkDifferenceOfQueries(q.query, disclosureQuery.query);
                        if (queryDifference < 0.3) {
                            similarQueries.add(q.query);
                        }
                        Log.info("Query difference: " + queryDifference); // TODO: delete
                    } else if (disclosureQueryDTO.getComparatorType() == QueryComparatorType.STRING) {
                        if (q.query.compareToIgnoreCase(disclosureQuery.query) == 0) {
                            similarQueries.add(q.query);
                        }
                    } else if (disclosureQueryDTO.getComparatorType() == QueryComparatorType.LEVENSHTEIN_DISTANCE) {
                        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();
                        int distance = levenshteinDistance.apply(q.query, disclosureQuery.query);
                        double similarity = 1 - (double) distance / Math.max(q.query.length(), disclosureQuery.query.length());
                        if (similarity >= 0.7) {
                            similarQueries.add(q.query);
                        }
                        Log.info("Similarity: " + similarity + " Distance: " + distance + ".");
                    }
                });
                // TODO: delete
                Log.info("Found " + similarQueries.size() + " similar queries by user: " + disclosureQueryDTO.getUserId());
                Log.info("Similar queries: " + similarQueries);
                /// /////////////////////
                if (similarQueries.isEmpty()) {
                    Log.info("No similar queries found by user: " + disclosureQueryDTO.getUserId());
                    disclosureQuery.status = QueryStatus.APPROVED;
                } else if (similarQueries.size() >= 10) {
                    Log.info("More than 10 similar queries found by user: " + disclosureQueryDTO.getUserId() + ". Denying query.");
                    disclosureQuery.message = "More than 10 similar queries found by user with id: " + disclosureQueryDTO.getUserId() + ". Denying query.";
                    disclosureQuery.status = QueryStatus.DENIED;
                    // create alert for denied query
                    Alert newAlert = new Alert();
                    newAlert.query = disclosureQuery;
                    newAlert.message = disclosureQuery.message;
                    newAlert.severity = AlertSeverity.SEVERE;
                    newAlert.type = AlertType.QUERY;
                    newAlert.isResolved = false;
                    newAlert.persist();
                } else if (similarQueries.size() >= 3) {
                    Log.info(similarQueries.size() + " similar queries found by user: " + disclosureQueryDTO.getUserId());
                    String tablesString = disclosureQueryDTO.getQuery()
                            .substring(
                                    disclosureQueryDTO.getQuery().indexOf("FROM")+4,
                                    disclosureQueryDTO.getQuery().contains("WHERE") ? disclosureQueryDTO.getQuery().indexOf("WHERE") : disclosureQueryDTO.getQuery().length());
                    List<String> policyNameList = Stream.of(tablesString.split(","))
                            .map(pt -> {
                                String tableName = pt.trim();
                                if (pt.contains("as")){
                                    tableName = pt.substring(0, pt.indexOf("as")).trim();
                                } else if (pt.contains("AS")) {
                                    tableName = pt.substring(0, pt.indexOf("AS")).trim();
                                }
                                tableName = tableName.replaceAll(";", "");
                                return tableName.trim();
                            }).toList();

                    List<Policy> policyList = policyRepository.findByNames(policyNameList);
                    if (policyList.isEmpty() || policyList.size() != policyNameList.size()) {
                        throw new RuntimeException("Error while retrieving policies for policy name list: " + policyNameList + ".");
                    }
                    List<Policy> nonMaterializedPolicies = policyList.stream().filter(p -> p.materializedViewName == null).toList();
                    nonMaterializedPolicies.forEach(policyService::createMaterializedViewForExistingPolicy);
                    List<String> newPolicyNameList = policyList.stream().map(p -> p.materializedViewName).toList();
                    String newTables = String.join(", ", newPolicyNameList);
                    String newQuery = disclosureQueryDTO.getQuery().replace(tablesString, " " + newTables + " ");
                    disclosureQuery.status = QueryStatus.MODIFIED;
                    disclosureQuery.message = "User: " + disclosureQueryDTO.getUserId() + " has submitted " + similarQueries.size() + " similar queries. Modified query to use materialized views in order to prevent Data Reconstruction attack.";
                    disclosureQuery.query = newQuery;
                    Log.info("Changed query of user: " + disclosureQueryDTO.getUserId() + " to: " + newQuery + ".");
                    // create an alert for a modified user
                    Alert newAlert = new Alert();
                    newAlert.query = disclosureQuery;
                    newAlert.message = disclosureQuery.message;
                    newAlert.severity = AlertSeverity.WARNING;
                    newAlert.type = AlertType.QUERY;
                    newAlert.isResolved = false;
                    newAlert.persist();


                } else {
                    // create an alert for less than 3 similar queries
                    disclosureQuery.status = QueryStatus.SUSPECT;
                    disclosureQuery.message = "User: " + disclosureQueryDTO.getUserId() + " has submitted " + similarQueries.size() + " similar queries. Suspect for Data Reconstruction Attack";
                    // create alert for a suspect user
                    Alert newAlert = new Alert();
                    newAlert.query = disclosureQuery;
                    newAlert.message = disclosureQuery.message;
                    newAlert.severity = AlertSeverity.WARNING;
                    newAlert.type = AlertType.QUERY;
                    newAlert.isResolved = false;
                    newAlert.persist();
                }
            } else { // TODO: adjust for other types of attacks
                disclosureQuery.status = QueryStatus.APPROVED;
            }
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


    // returns 0 if queries are equal, 1 if they are totally different
    // if tables are equal, columns and where clauses are checked and the ratio is measured from all three
    // if tables and columns are equal, the where clauses are not checked and the ratio is returned as 0
    // if tables are totally different, the ratio is returned as 1
    // if columns are totally different, the ratio is returned as 1
    private Double checkDifferenceOfQueries(String previousQuery, String currentQuery) {
        double differentTableRatio = 0.0;

        if (previousQuery.equals(currentQuery)) {
            return 0.0;
        }
        String previousQueryTables = previousQuery.substring(previousQuery.indexOf("FROM")+4, previousQuery.contains("WHERE") ? previousQuery.indexOf("WHERE") : previousQuery.length()).trim();
        String currentQueryTables = currentQuery.substring(currentQuery.indexOf("FROM")+4, currentQuery.contains("WHERE") ? currentQuery.indexOf("WHERE") : currentQuery.length()).trim();
        List<String> previousTableList = Arrays.stream(previousQueryTables.split(","))
                .map(pt -> {
                    String tableName = pt.trim();
                    if (pt.contains("as")){
                        tableName = pt.substring(0, pt.indexOf("as")).trim();
                    } else if (pt.contains("AS")) {
                        tableName = pt.substring(0, pt.indexOf("AS")).trim();
                    }
                    tableName = tableName.replaceAll(";", "");
                    return tableName.trim();
                }).toList();
        List<String> fullPreviousTableList = policyRepository.findByNames(previousTableList).stream().flatMap(p -> {
            if (p.materializedViewName != null && p.viewName != null) return Stream.of(p.materializedViewName, p.viewName);
            else if (p.materializedViewName != null) return Stream.of(p.materializedViewName);
            else return Stream.ofNullable(p.viewName);
        }).toList();

        List<String> currentTableList = Arrays.stream(currentQueryTables.split(","))
                .map(pt -> {
                    String tableName = pt.trim();
                    if (pt.contains("as")){
                        tableName = pt.substring(0, pt.indexOf("as")).trim();
                    } else if (pt.contains("AS")) {
                        tableName = pt.substring(0, pt.indexOf("AS")).trim();
                    }
                    tableName = tableName.replaceAll(";", "");
                    return tableName.trim();
                }).toList();

        List<String> differentTables = currentTableList.stream().filter(t -> !fullPreviousTableList.contains(t)).toList();


        if (previousQueryTables.equals(currentQueryTables) || differentTables.isEmpty()) {
            return compareTableColumnsAndWhereClauses(previousQuery, currentQuery, differentTableRatio, previousTableList, currentTableList);
        } else {
            differentTableRatio = (double) differentTables.size() / currentTableList.size();
            if (differentTableRatio == 1) {
                return 1.0;
            } else {
                return compareTableColumnsAndWhereClauses(previousQuery, currentQuery, differentTableRatio, previousTableList, currentTableList);
            }
        }
    }

    private double compareTableColumnsAndWhereClauses(String previousQuery, String currentQuery, double differentTableRatio, List<String> previousTableList, List<String> currentTableList) {
        double differentColumnRatio;
        double differentWhereClauseRatio = 0.0;
        // comparing query columns
        String previousQueryColumns = previousQuery.substring(previousQuery.indexOf("SELECT")+6, previousQuery.indexOf("FROM")).trim();
        String currentQueryColumns = currentQuery.substring(currentQuery.indexOf("SELECT")+6, currentQuery.indexOf("FROM")).trim();
        List<String> previousColumnList = Arrays.stream(previousQueryColumns.split(","))
                .map(pc -> {
                    String columnName = pc.trim();
                    if (pc.contains("as")){
                        columnName = pc.substring(0, pc.indexOf("as")).trim();
                    } else if (pc.contains("AS")) {
                        columnName = pc.substring(0, pc.indexOf("AS")).trim();
                    }
                    if (columnName.contains(".")) {
                        columnName = columnName.substring(columnName.indexOf(".")+1);
                    }
                    columnName = columnName.replaceAll(";", "");
                    return columnName.trim();
                }).toList();
        List<String> currentColumnList = Arrays.stream(currentQueryColumns.split(","))
                .map(cc -> {
                    String columnName = cc.trim();
                    if (cc.contains("as")){
                        columnName = cc.substring(0, cc.indexOf("as")).trim();
                    } else if (cc.contains("AS")) {
                        columnName = cc.substring(0, cc.indexOf("AS")).trim();
                    }
                    columnName = columnName.replaceAll(";", "");
                    if (columnName.contains(".")) {
                        columnName = columnName.substring(columnName.indexOf(".")+1);
                    }
                    return columnName.trim();
                }).toList();

        if (previousQueryColumns.contains("*")) {
            previousColumnList = dataDBService.getColumnNamesOfViews(previousTableList);
        }
        if (currentQueryColumns.contains("*")) {
            currentColumnList = dataDBService.getColumnNamesOfViews(currentTableList);
        }
        final List<String> finalPreviousColumnList = previousColumnList;
        List<String> differentColumns = currentColumnList.stream().filter(c -> !finalPreviousColumnList.contains(c)).toList();

        if (previousQueryColumns.equals(currentQueryColumns)) {
            return 0.0;
        } else {
            if (differentColumns.isEmpty()) {
                return 0.0;
            } else {
                differentColumnRatio = (double) differentColumns.size() / currentColumnList.size();
                if (differentColumnRatio == 1) {
                    return 1.0;
                } else if (previousQuery.contains("WHERE") && currentQuery.contains("WHERE")){
                    // comparing where clauses
                    String previousQueryWhereClause = previousQuery.substring(previousQuery.indexOf("WHERE")+5).trim();
                    String currentQueryWhereClause = currentQuery.substring(currentQuery.indexOf("WHERE")+5).trim();
                    if (!previousQueryWhereClause.equals(currentQueryWhereClause)) {
                        List<String> previousWhereClauseList = Arrays.stream(previousQueryWhereClause.split("(?i)and")).map(String::trim).toList();
                        List<String> currentWhereClauseList = Arrays.stream(currentQueryWhereClause.split("(?i)and")).map(String::trim).toList();
                        List<String> differentWhereClauses = currentWhereClauseList.stream().filter(c -> !previousWhereClauseList.contains(c)).toList();
                        if (!differentWhereClauses.isEmpty()) {
                            differentWhereClauseRatio = (double) differentWhereClauses.size() / currentWhereClauseList.size();
                        }
                    }
                }
                return (differentColumnRatio + differentTableRatio + differentWhereClauseRatio) / 3;
            }
        }
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
