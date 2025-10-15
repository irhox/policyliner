package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.constants.*;
import de.tub.dima.policyliner.database.data.DataDBService;
import de.tub.dima.policyliner.database.policyliner.*;
import de.tub.dima.policyliner.dto.*;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class QueryService {

    private final DisclosureQueryRepository disclosureQueryRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final PolicyService policyService;
    private final DataDBService dataDBService;
    private final QueryParserService queryParserService;

    public QueryService(
            DisclosureQueryRepository disclosureQueryRepository,
            PolicyRepository policyRepository,
            UserRepository userRepository,
            PolicyService policyService,
            DataDBService dataDBService,
            QueryParserService queryParserService
    ) {
        this.disclosureQueryRepository = disclosureQueryRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.policyService = policyService;
        this.dataDBService = dataDBService;
        this.queryParserService = queryParserService;
    }

    // Queries of each user are run, and the results are compared to each other.
    // Based on results similarity, there are alerts created.
    @Scheduled(every = "{query.evaluation.interval}")
    @RunOnVirtualThread
    public void offlineQueryAnalysis() {
        Log.info("Offline Query Analysis started.");
        List<User> users = userRepository.listAll();
        for (User user : users) {
            List<DisclosureQuery> userQueries = disclosureQueryRepository.findNewQueriesByUserId(user.id);
            for (int i = 0; i < userQueries.size(); i++) {
                compareUserQueryResults(i, userQueries);
            }
        }
        Log.info("Offline Query Analysis finished.");
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void compareUserQueryResults(int i, List<DisclosureQuery> userQueries) {
        DisclosureQuery currentDisclosureQuery = userQueries.get(i);
        boolean wasSuspect = currentDisclosureQuery.status == QueryStatus.SUSPECT || currentDisclosureQuery.status == QueryStatus.MODIFIED || currentDisclosureQuery.status == QueryStatus.DENIED;
        boolean isApproved = true;
        for (int j = i+1; j < userQueries.size(); j++) {
            DisclosureQuery prevDisclosureQuery = userQueries.get(j);
            Set<String> iColumns = queryParserService.getColumnNames(currentDisclosureQuery.query);
            Set<String> jColumns = queryParserService.getColumnNames(prevDisclosureQuery.query);
            String sameColumns = " " + String.join(", ", iColumns.stream().filter(jColumns::contains).toList()) + " ";
            String query1 = currentDisclosureQuery.query.replace(
                    currentDisclosureQuery.query.substring(
                            currentDisclosureQuery.query.indexOf("SELECT")+6,
                            currentDisclosureQuery.query.indexOf("FROM")),
                    sameColumns);
            String query2 = prevDisclosureQuery.query.replace(
                    prevDisclosureQuery.query.substring(
                            prevDisclosureQuery.query.indexOf("SELECT")+6,
                            prevDisclosureQuery.query.indexOf("FROM")),
                    sameColumns);
            QueryResultsComparisonDTO comparisonResults = dataDBService.compareWhereClauseResults(query1, query2);
            long diffCountSum = comparisonResults.getCurrentCount() + comparisonResults.getPreviousCount();
            long totalCountSum = comparisonResults.getCurrentTotalCount() + comparisonResults.getPreviousTotalCount();
            if (diffCountSum == 0) {
                currentDisclosureQuery.status = QueryStatus.SUSPECT;
                prevDisclosureQuery.status = QueryStatus.SUSPECT;
                isApproved = false;
                Log.info("Queries are equal: " + currentDisclosureQuery.query + " and " + prevDisclosureQuery.query);

                Alert newAlert = new Alert();
                newAlert.type = AlertType.OFFLINE_QUERY;
                newAlert.severity = AlertSeverity.SEVERE;
                newAlert.message = """
                        Query (%s) and (%s) of User (%s) have equal results. Suspect of Data Reconstruction Attack.
                        """.formatted(currentDisclosureQuery.query, currentDisclosureQuery.query, currentDisclosureQuery.user.id);
                newAlert.persist();
                currentDisclosureQuery.alerts.add(newAlert);
                prevDisclosureQuery.alerts.add(newAlert);

            } else if (totalCountSum - diffCountSum == 0) {
                Log.info("Queries are totally different: " + currentDisclosureQuery.query + " and " + prevDisclosureQuery.query);
            } else {
                double resultsRatio = (double) diffCountSum / totalCountSum;
                Log.info("Results ratio: " + resultsRatio);
                if (resultsRatio <= 0.5) {
                    currentDisclosureQuery.status = QueryStatus.SUSPECT;
                    prevDisclosureQuery.status = QueryStatus.SUSPECT;
                    isApproved = false;
                    Log.info("Queries are similar: " + currentDisclosureQuery.query + " and " + prevDisclosureQuery.query);

                    Alert newAlert = new Alert();
                    newAlert.type = AlertType.OFFLINE_QUERY;
                    newAlert.severity = AlertSeverity.WARNING;
                    newAlert.message = """
                        Query (%s) and (%s) of User (%s) have similar results with ratio: %s. Suspect of Data Reconstruction Attack.
                        """.formatted(currentDisclosureQuery.query, currentDisclosureQuery.query, currentDisclosureQuery.user.id, resultsRatio);
                    newAlert.persist();
                    currentDisclosureQuery.alerts.add(newAlert);
                    prevDisclosureQuery.alerts.add(newAlert);
                }
            }
        }
        if (isApproved && wasSuspect) {
            Alert newAlert = new Alert();
            newAlert.message = """
                    Query (%s) of User (%s) was flagged as %s by the online query analysis.
                    Offline Query Analysis has found out that this was a false positive and the query has been approved.
                    """.formatted(currentDisclosureQuery.query, currentDisclosureQuery.user.id, currentDisclosureQuery.status.name());
            newAlert.severity = AlertSeverity.INFO;
            newAlert.type = AlertType.OFFLINE_QUERY;
            newAlert.persist();
            currentDisclosureQuery.alerts.add(newAlert);
            currentDisclosureQuery.status = QueryStatus.APPROVED;
        }
        currentDisclosureQuery.inspectionStatus = QueryInspectionStatus.INSPECTED;
        DisclosureQuery.getEntityManager().merge(currentDisclosureQuery);
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
                        double queryDifference;
                        try {
                            queryDifference = checkDifferenceOfQueries(q.query, disclosureQuery.query);
                        } catch (JSQLParserException e) {
                            throw new RuntimeException(e);
                        }
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
                    newAlert.message = disclosureQuery.message;
                    newAlert.severity = AlertSeverity.SEVERE;
                    newAlert.type = AlertType.ONLINE_QUERY;
                    newAlert.isResolved = false;
                    newAlert.persist();
                    disclosureQuery.alerts.add(newAlert);
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
                    newAlert.message = disclosureQuery.message;
                    newAlert.severity = AlertSeverity.WARNING;
                    newAlert.type = AlertType.ONLINE_QUERY;
                    newAlert.isResolved = false;
                    newAlert.persist();
                    if (disclosureQuery.alerts == null) {
                        disclosureQuery.alerts = new ArrayList<>();
                    }
                    disclosureQuery.alerts.add(newAlert);
                } else {
                    // create an alert for less than 3 similar queries
                    disclosureQuery.status = QueryStatus.SUSPECT;
                    disclosureQuery.message = "User: " + disclosureQueryDTO.getUserId() + " has submitted " + similarQueries.size() + " similar queries. Suspect for Data Reconstruction Attack";
                    // create alert for a suspect user
                    Alert newAlert = new Alert();
                    newAlert.message = disclosureQuery.message;
                    newAlert.severity = AlertSeverity.WARNING;
                    newAlert.type = AlertType.ONLINE_QUERY;
                    newAlert.isResolved = false;
                    newAlert.persist();
                    if (disclosureQuery.alerts == null) {
                        disclosureQuery.alerts = new ArrayList<>();
                    }
                    disclosureQuery.alerts.add(newAlert);
                }
            } else { // TODO: adjust for other types of attacks
                disclosureQuery.status = QueryStatus.APPROVED;
            }
        }

        disclosureQuery.persist();

        return convertToQueryResponseDTO(disclosureQuery);
    }

    // TODO: Implement sorting
    public PagedResponseDTO<QueryResponseDTO> searchQueries(SearchDTO searchDTO) {
        PanacheQuery<DisclosureQuery> disclosurePanacheQuery = disclosureQueryRepository.findFilteredQueries(searchDTO.getFilter());

        List<QueryResponseDTO> queryList = disclosurePanacheQuery.page(
                Page.of(searchDTO.getPageNumber(), searchDTO.getPageSize())
        ).list().stream().map(this::convertToQueryResponseDTO).toList();

        return createPagedResponseDTO(queryList, searchDTO, disclosurePanacheQuery.count());
    }

    public QueryResponseDTO getQueryById(String queryId) {
        DisclosureQuery disclosureQuery = disclosureQueryRepository.findById(queryId);
        return convertToQueryResponseDTO(disclosureQuery);
    }


    // returns 0 if queries are equal, 1 if they are totally different;
    // if tables and columns have some similarities or are equal, where clause results are also considered;
    // if tables are totally different, the ratio is returned as 1;
    // if columns are totally different, the ratio is returned as 1;
    private Double checkDifferenceOfQueries(String previousQuery, String currentQuery) throws JSQLParserException {
        double differentTableRatio = 0.0;

        if (previousQuery.equals(currentQuery)) {
            return 0.0;
        }
        String previousQueryTables = previousQuery.substring(previousQuery.indexOf("FROM")+4, previousQuery.contains("WHERE") ? previousQuery.indexOf("WHERE") : previousQuery.length()).trim();
        String currentQueryTables = currentQuery.substring(currentQuery.indexOf("FROM")+4, currentQuery.contains("WHERE") ? currentQuery.indexOf("WHERE") : currentQuery.length()).trim();
        Set<String> previousTableSet = queryParserService.getTableNames(previousQuery);
        List<String> fullPreviousTableList = policyRepository.findByNames(previousTableSet).stream().flatMap(p -> {
            if (p.materializedViewName != null && p.viewName != null) return Stream.of(p.materializedViewName, p.viewName);
            else if (p.materializedViewName != null) return Stream.of(p.materializedViewName);
            else return Stream.ofNullable(p.viewName);
        }).toList();

        Set<String> currentTableSet = queryParserService.getTableNames(currentQuery);

        Set<String> differentTables = currentTableSet.stream().filter(t -> !fullPreviousTableList.contains(t)).collect(Collectors.toSet());

        if (previousQueryTables.equals(currentQueryTables) || differentTables.isEmpty()) {
            return compareTableColumnsAndWhereClauses(previousQuery, currentQuery, differentTableRatio, previousTableSet, currentTableSet);
        } else {
            differentTableRatio = (double) differentTables.size() / currentTableSet.size();
            if (differentTableRatio == 1) {
                return 1.0;
            } else {
                return compareTableColumnsAndWhereClauses(previousQuery, currentQuery, differentTableRatio, previousTableSet, currentTableSet);
            }
        }
    }

    private double compareTableColumnsAndWhereClauses(String previousQuery, String currentQuery, double differentTableRatio, Set<String> previousTableSet, Set<String> currentTableSet) {
        double differentColumnRatio = 0.0;
        double differentWhereClauseRatio = 0.0;
        // comparing query columns
        String previousQueryColumns = previousQuery.substring(previousQuery.indexOf("SELECT")+6, previousQuery.indexOf("FROM")).trim();
        String currentQueryColumns = currentQuery.substring(currentQuery.indexOf("SELECT")+6, currentQuery.indexOf("FROM")).trim();
        Set<String> previousColumnSet = queryParserService.getColumnNames(previousQuery);
        Set<String> currentColumnSet = queryParserService.getColumnNames(currentQuery);

        if (previousQueryColumns.contains("*")) {
            previousColumnSet = dataDBService.getColumnNamesOfViews(previousTableSet);
        }
        if (currentQueryColumns.contains("*")) {
            currentColumnSet = dataDBService.getColumnNamesOfViews(currentTableSet);
        }
        final Set<String> finalPreviousColumnSet = previousColumnSet;
        List<String> differentColumns = currentColumnSet.stream().filter(c -> !finalPreviousColumnSet.contains(c)).toList();
        if (!differentColumns.isEmpty()) {
            differentColumnRatio = (double) differentColumns.size() / currentColumnSet.size();
        }
        if (previousQuery.contains("WHERE") && currentQuery.contains("WHERE")){
            // comparing where clauses
            String previousQueryWhereClause = previousQuery.substring(previousQuery.indexOf("WHERE")+5).trim();
            String currentQueryWhereClause = currentQuery.substring(currentQuery.indexOf("WHERE")+5).trim();
            if (!previousQueryWhereClause.equals(currentQueryWhereClause)) {
                Set<String> previousWhereClauseSet = queryParserService.getWhereClauses(previousQuery);
                Set<String> currentWhereClauseSet = queryParserService.getWhereClauses(currentQuery);
                Set<String> differentWhereClauses = currentWhereClauseSet.stream().filter(c -> !previousWhereClauseSet.contains(c)).collect(Collectors.toSet());
                if (!differentWhereClauses.isEmpty()) {
                    differentWhereClauseRatio = (double) differentWhereClauses.size() / currentWhereClauseSet.size();
                }
            }
        }
        return (differentColumnRatio + differentTableRatio + differentWhereClauseRatio) / 3;
    }

    private PagedResponseDTO<QueryResponseDTO> createPagedResponseDTO(List<QueryResponseDTO> queryList, SearchDTO searchDTO, Long totalElements) {
        PagedResponseDTO<QueryResponseDTO> page = new PagedResponseDTO<>();
        page.setCurrentPage(searchDTO.getPageNumber());
        page.setPageSize(searchDTO.getPageSize());
        page.setElements(queryList);
        page.setTotalElements(totalElements);
        page.setTotalPages((int) (totalElements/searchDTO.getPageSize()));

        return page;
    }

    private QueryResponseDTO convertToQueryResponseDTO(DisclosureQuery disclosureQuery) {
        return new QueryResponseDTO(
                disclosureQuery.getId(),
                disclosureQuery.user.id,
                disclosureQuery.query,
                disclosureQuery.status,
                disclosureQuery.inspectionStatus,
                disclosureQuery.message,
                disclosureQuery.alerts.stream().filter(a -> !a.isResolved).collect(Collectors.toMap(Alert::getId, a -> a.severity, (existing, replacement) -> existing)),
                disclosureQuery.createdAt);
    }
}
