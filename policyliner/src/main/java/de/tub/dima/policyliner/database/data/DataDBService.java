package de.tub.dima.policyliner.database.data;

import de.tub.dima.policyliner.dto.CreatePolicyDTO;
import de.tub.dima.policyliner.dto.ViewAttributeDTO;
import io.quarkus.hibernate.orm.PersistenceUnit;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class DataDBService {

    @Inject
    @PersistenceUnit("data")
    EntityManager em;

    @SuppressWarnings("unchecked")
    public List<UserDefinedFunction> getUserDefinedFunctions() {
        String nativeQuery = "select p.proname as functionName, pg_get_function_arguments(p.oid) as functionArguments " +
                "from pg_proc p left join pg_namespace n on p.pronamespace = n.oid " +
                "where n.nspname not in ('pg_catalog', 'information_schema') " +
                "order by functionName";

        List<List<String>> resultList = em.createNativeQuery(nativeQuery, List.class).getResultList();
        return resultList.stream()
                .map(list -> new UserDefinedFunction(list.getFirst(), list.get(1))).toList();
    }

    @SuppressWarnings("unchecked")
    public List<MaterializedView> getMaterializedViews() {
        String nativeQuery = "select matviewname as viewName, ispopulated as isPopulated, definition from pg_matviews order by viewName;";

        List<List<Object>> resultList = em.createNativeQuery(nativeQuery, List.class).getResultList();
        return resultList.stream()
                .map(list ->
                        new MaterializedView(
                                list.getFirst().toString(),
                                Objects.equals(list.get(1).toString(), "true"),
                                list.get(2).toString()))
                .toList();
    }

    @SuppressWarnings("unchecked")
    public List<TableInformation> getTables() {
        String nativeQuery = "SELECT table_name as tableName, table_schema as tableSchema FROM information_schema.tables WHERE table_schema = 'public';";
        List<List<String>> resultList = em.createNativeQuery(nativeQuery, List.class).getResultList();
        return resultList.stream()
                .map(list ->
                        new TableInformation(list.getFirst(), list.get(1)))
                .toList();
    }

    // TODO: extend for multiple tables
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public String createPolicy(CreatePolicyDTO createPolicyDTO) {
        Log.info("Creating policy " + createPolicyDTO.getPolicyName());
        // First, drop the existing view if it exists
        String dropQuery = "DROP MATERIALIZED VIEW IF EXISTS " + createPolicyDTO.getPolicyName() + " CASCADE;";
        Query dropViewQuery = em.createNativeQuery(dropQuery);
        dropViewQuery.executeUpdate();

        StringBuilder policyCreationQuery = new StringBuilder("CREATE MATERIALIZED VIEW ")
                .append(createPolicyDTO.getPolicyName()).append(" AS SELECT ");
        for (ViewAttributeDTO attribute: createPolicyDTO.getColumns()) {
            if (attribute.getFunctionName() != null) {
                policyCreationQuery.append(attribute.getFunctionName()).append("(").append(attribute.getTableColumnName());
                if (attribute.getFunctionArguments() != null && !attribute.getFunctionArguments().isEmpty()) {
                    attribute.getFunctionArguments().forEach(arg -> policyCreationQuery.append(", ").append(arg));
                }
                policyCreationQuery.append(") AS ").append(attribute.getViewColumnName());
            } else {
                policyCreationQuery.append(attribute.getTableColumnName()).append(" AS ").append(attribute.getTableColumnName());
            }
            if (createPolicyDTO.getColumns().indexOf(attribute) < (createPolicyDTO.getColumns().size() - 1)) {
                policyCreationQuery.append(", ");
            }
        }
        policyCreationQuery.append(" FROM ").append(createPolicyDTO.getTables().getFirst().getTableName());
        AtomicInteger tableListCounter = new AtomicInteger();
        if (createPolicyDTO.getTables().size() > 1) {
            createPolicyDTO.getTables().stream().skip(1).forEach(table -> {
                policyCreationQuery.append(" JOIN ").append(table.getTableName());
                policyCreationQuery.append(" ON ")
                        .append(table.getTableName())
                        .append(".")
                        .append(table.getForeignKey())
                        .append(" = ")
                        .append(createPolicyDTO.getTables().get(tableListCounter.get()).getTableName())
                        .append(".").append(createPolicyDTO.getTables().get(tableListCounter.get()).getPrimaryKey());

                tableListCounter.getAndIncrement();
            });
        }
        Query policyQuery = em.createNativeQuery(policyCreationQuery.toString());
        Log.info("Policy creation query: " + policyQuery.unwrap(org.hibernate.query.Query.class).getQueryString());
        policyQuery.executeUpdate();
        Log.info("Policy " + createPolicyDTO.getPolicyName() + " created successfully");

        return policyQuery.unwrap(org.hibernate.query.Query.class).getQueryString();
    }



}
