package de.tub.dima.policyliner.database.data.metrics;

import de.tub.dima.policyliner.entities.TClosenessReport;
import de.tub.dima.policyliner.entities.TClosenessReports;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@ApplicationScoped
public class TClosenessCalculator implements PrivacyMetricCalculator<TClosenessReports>{

    @Inject
    @PersistenceUnit("data")
    EntityManager em;

    @ConfigProperty(name = "policyLiner.privacy-metric.sampling-limit", defaultValue = "1000000")
    int sampleSizeLimit;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public TClosenessReports getPrivacyMetricReportOfTable(String viewName, List<String> columns, List<String> sensitiveAttributes) {
        String columnString = String.join(",", columns);
        List<TClosenessReport> tableReportList = new ArrayList<>();

        for (String sensitiveAttribute : sensitiveAttributes) {
            Map<String, BigDecimal> distribution = getGlobalDistribution(viewName, sensitiveAttribute);
            String queryString = buildTClosenessQuery(viewName, columnString, sensitiveAttribute);

            Query query = em.createNativeQuery(queryString);
            List<Object[]> results = query.getResultList();
            TClosenessReport report = processTClosenessResults(viewName, columns, results, distribution, BigDecimal.valueOf(0.2), sensitiveAttribute);
            tableReportList.add(report);
        }

        return new TClosenessReports(tableReportList);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Map<String, BigDecimal> getGlobalDistribution(String viewName, String sensitiveAttribute) {
        String queryString = """
                SELECT %s, COUNT(*) * 1.0 / (SELECT COUNT(*) FROM %s) AS proportion
                FROM %s
                GROUP BY %s
                ORDER BY RANDOM() LIMIT %d
                """.formatted(sensitiveAttribute, viewName, viewName, sensitiveAttribute, sampleSizeLimit);

        Query query = em.createNativeQuery(queryString);
        List<Object[]> results = query.getResultList();

        Map<String, BigDecimal> distribution = new HashMap<>();
        for (Object[] row : results) {
            String value = row[0] != null ? row[0].toString() : "NULL";
            BigDecimal proportion = new BigDecimal(row[1].toString());
            distribution.put(value, proportion);
        }
        return distribution;
    }

    private String buildTClosenessQuery(String viewName, String qiColumns, String sensitiveAttribute) {
        // Determine if the sensitive attribute is already in QI columns
        List<String> qiColumnList = Arrays.asList(qiColumns.split(","));
        boolean sensitiveInQI = qiColumnList.stream()
                .anyMatch(col -> col.trim().equalsIgnoreCase(sensitiveAttribute.trim()));

        String allColumns;
        if (sensitiveInQI) {
            allColumns = qiColumns;
        } else {
            allColumns = qiColumns + ", " + sensitiveAttribute;
        }

        return """
                WITH equivalence_classes AS (
                    SELECT %s, COUNT(*) AS class_size
                    FROM %s
                    GROUP BY %s
                    ORDER BY RANDOM() LIMIT %d
                ),
                class_distribution AS (
                    SELECT %s,
                           COUNT(*) * 1.0 / SUM(COUNT(*)) OVER (PARTITION BY %s) AS proportion,
                           SUM(COUNT(*)) OVER (PARTITION BY %s) AS class_size
                    FROM %s
                    GROUP BY %s
                    ORDER BY RANDOM() LIMIT %d
                )
                SELECT
                    %s,
                    %s,
                    cd.proportion,
                    cd.class_size
                FROM class_distribution cd
                """.formatted(
                allColumns, viewName, allColumns, sampleSizeLimit,
                allColumns, qiColumns, qiColumns, viewName, allColumns, sampleSizeLimit,
                addPrefixToColumns(qiColumnList, "cd"),
                addPrefixToColumns(List.of(sensitiveAttribute), "cd")
        );
    }

    private TClosenessReport processTClosenessResults(String viewName, List<String> qiColumns, List<Object[]> results,
                                                      Map<String, BigDecimal> globalDistribution,
                                                      BigDecimal threshold,
                                                      String sensitiveAttribute) {
        Map<String, Map<String, BigDecimal>> equivalenceClassDistributions = new HashMap<>();
        Map<String, Long> equivalenceClassSizes = new HashMap<>();

        // Group results by equivalence class
        for (Object[] row : results) {
            // Build composite key from all QI columns
            StringBuilder qiGroupBuilder = new StringBuilder();
            for (int i = 0; i < qiColumns.size(); i++) {
                if (i > 0) qiGroupBuilder.append("|");
                qiGroupBuilder.append(row[i] != null ? row[i].toString() : "NULL");
            }
            String qiGroup = qiGroupBuilder.toString();

            String sensitiveValue = row[qiColumns.size()] != null ? row[qiColumns.size()].toString() : "NULL";
            BigDecimal proportion = new BigDecimal(row[qiColumns.size()+1].toString());
            Long classSize = ((Number) row[qiColumns.size()+2]).longValue();

            equivalenceClassDistributions
                    .computeIfAbsent(qiGroup, k -> new HashMap<>())
                    .put(sensitiveValue, proportion);
            equivalenceClassSizes.put(qiGroup, classSize);
        }

        Map<String, BigDecimal> distances = new HashMap<>();
        BigDecimal maxDistance = BigDecimal.ZERO;
        BigDecimal sumDistances = BigDecimal.ZERO;
        long violatingClasses = 0;

        for (Map.Entry<String, Map<String, BigDecimal>> entry : equivalenceClassDistributions.entrySet()) {
            String qiGroup = entry.getKey();
            Map<String, BigDecimal> classDistribution = entry.getValue();

            BigDecimal distance = calculateEfficientEMD(classDistribution, globalDistribution);
            distances.put(qiGroup, distance);

            if (distance.compareTo(maxDistance) > 0) {
                maxDistance = distance;
            }
            sumDistances = sumDistances.add(distance);

            if (threshold != null && distance.compareTo(threshold) > 0) {
                violatingClasses++;
            }
        }

        long totalClasses = equivalenceClassDistributions.size();
        BigDecimal avgDistance = totalClasses > 0
                ? sumDistances.divide(BigDecimal.valueOf(totalClasses), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new TClosenessReport(
                viewName,
                sensitiveAttribute,
                maxDistance,
                avgDistance,
                totalClasses,
                violatingClasses,
                distances
        );
    }

    private BigDecimal calculateEfficientEMD(Map<String, BigDecimal> distribution1,
                                             Map<String, BigDecimal> distribution2) {
        Set<String> allValues = new HashSet<>();
        allValues.addAll(distribution1.keySet());
        allValues.addAll(distribution2.keySet());
        List<String> orderedValues = new ArrayList<>(allValues);
        Collections.sort(orderedValues);
        int m = orderedValues.size();

        if (m <= 1) {
            return BigDecimal.ZERO;
        }

        BigDecimal[] p = new BigDecimal[m];
        BigDecimal[] q = new BigDecimal[m];

        for (int i = 0; i < m; i++) {
            String value = orderedValues.get(i);
            p[i] = distribution1.getOrDefault(value, BigDecimal.ZERO);
            q[i] = distribution2.getOrDefault(value, BigDecimal.ZERO);
        }

        // Efficient EMD Algorithm (Dosselmann et al.)
        // EMD ← 0
        BigDecimal emd = BigDecimal.ZERO;

        // S ← 0  (initialize current sum)
        BigDecimal sum = BigDecimal.ZERO;

        // for i = 1 to m do
        for (int i = 0; i < m; i++) {
            // S ← S + (pi − qi)
            sum = sum.add(p[i].subtract(q[i]));

            // EMD ← EMD + |S|
            emd = emd.add(sum.abs());
        }

        // EMD ← EMD/(m − 1)
        return emd.divide(BigDecimal.valueOf(m - 1), 6, RoundingMode.HALF_UP);
    }

    private String addPrefixToColumns(List<String> columnList, String prefix) {
        return String.join(", ", columnList.stream().map(col -> prefix + "." + col).toList());
    }

}
