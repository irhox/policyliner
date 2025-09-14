package de.tub.dima.policyliner.database.data;

import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.dto.CreatePolicyDTO;
import de.tub.dima.policyliner.dto.TableInfoDTO;
import de.tub.dima.policyliner.dto.ViewAttributeDTO;
import io.quarkus.hibernate.orm.PersistenceUnit;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
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

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<String> getColumnNamesOfViews(List<String> viewNameList) {
        String nativeQueryString = "SELECT att.attname as column_name FROM pg_catalog.pg_attribute att join pg_catalog.pg_class mv ON mv.oid = att.attrelid" +
                            " where (mv.relkind = 'm' OR mv.relkind = 'v')" +
                            " AND mv.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public')" +
                            " AND not att.attisdropped" +
                            " AND att.attnum > 0" +
                            " AND mv.relname in :viewNameList;";

        Query nativeQuery = em.createNativeQuery(nativeQueryString, List.class);
        nativeQuery.setParameter("viewNameList", viewNameList);
        List<List<Object>> resultList = nativeQuery.getResultList();
        return resultList.stream()
                .map(o -> o.getFirst().toString())
                .toList();

    }

    @SuppressWarnings("unchecked")
    public List<View> getViews() {
        String nativeQuery = "select table_name as viewName from INFORMATION_SCHEMA.views WHERE table_schema = ANY (current_schemas(false))";

        List<List<Object>> resultList = em.createNativeQuery(nativeQuery, List.class).getResultList();
        return resultList.stream()
                .map(list ->
                        new View(list.getFirst().toString()))
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

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Map<String, String> createMaterializedViewFromExistingPolicy(Policy policy){
        final String currentPolicyQuery = policy.policy;
        String newPolicyQuery = currentPolicyQuery.replace("CREATE OR REPLACE VIEW", "CREATE MATERIALIZED VIEW IF NOT EXISTS");
        String materializedViewName = policy.viewName + "_materialized";
        List <MaterializedView> materializedViews = getMaterializedViews();
        if (materializedViews.stream().map(MaterializedView::getViewName).toList().contains(materializedViewName)){
            Random random = new Random();
            materializedViewName = materializedViewName + random.nextInt(1000);
        }
        newPolicyQuery = newPolicyQuery.replace(policy.viewName, materializedViewName);
        Query materializedViewQuery = em.createNativeQuery(newPolicyQuery);
        materializedViewQuery.executeUpdate();
        Log.info("Materialized view " + materializedViewName + " created successfully.");
        return Map.of(materializedViewName, newPolicyQuery);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public String createPolicy(CreatePolicyDTO createPolicyDTO) {
        Log.info("Creating policy " + createPolicyDTO.getPolicyName());
        Random random = new Random();
        String viewType = createPolicyDTO.getIsMaterializedView() ? "MATERIALIZED VIEW" : "VIEW";
        // check if a view with the same name already exists
        List<MaterializedView> materializedViews = getMaterializedViews();
        if (materializedViews.stream().map(MaterializedView::getViewName).toList().contains(createPolicyDTO.getPolicyName())){
            createPolicyDTO.setPolicyName(createPolicyDTO.getPolicyName() + random.nextInt(1000));
        }
        List<View> views = getViews();
        if (views.stream().map(View::getViewName).toList().contains(createPolicyDTO.getPolicyName())){
            createPolicyDTO.setPolicyName(createPolicyDTO.getPolicyName() + random.nextInt(1000));
        }


        StringBuilder policyCreationQuery = viewType.equals("MATERIALIZED VIEW") ? new StringBuilder("CREATE MATERIALIZED VIEW IF NOT EXISTS ") : new StringBuilder("CREATE VIEW ");
        policyCreationQuery.append(createPolicyDTO.getPolicyName()).append(" AS SELECT ");
        for (ViewAttributeDTO attribute: createPolicyDTO.getColumns()) {
            if (attribute.getFunctionName() != null) {
                policyCreationQuery.append(attribute.getFunctionName()).append("(").append(attribute.getTableColumnName());
                if (attribute.getFunctionArguments() != null && !attribute.getFunctionArguments().isEmpty()) {
                    attribute.getFunctionArguments().forEach(arg -> policyCreationQuery.append(", ").append(arg));
                }
                policyCreationQuery.append(") AS ");
                String viewColumnName = attribute.getViewColumnName()
                        .replace("(", "_")
                        .replace(")", "")
                        .replace(" ", "");
                if (attribute.getViewColumnName().contains(",")) {
                    viewColumnName = viewColumnName.substring(0, viewColumnName.indexOf(","));
                }

                policyCreationQuery.append(viewColumnName);

            } else {
                policyCreationQuery.append(attribute.getTableColumnName()).append(" AS ").append(attribute.getViewColumnName() != null ? attribute.getViewColumnName() : attribute.getTableColumnName());
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
                TableInfoDTO secondTable = createPolicyDTO.getTables().get(tableListCounter.get());
                policyCreationQuery.append(" ON ")
                        .append(table.getTableName())
                        .append(".")
                        .append(table.getForeignKey())
                        .append(" = ")
                        .append(createPolicyDTO.getTables().get(tableListCounter.get()).getTableName())
                        .append(".").append(secondTable.getPrimaryKey() != null ? secondTable.getPrimaryKey() : secondTable.getForeignKey());

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
