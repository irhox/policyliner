package de.tub.dima.policyliner.database.data.metrics;

import de.tub.dima.policyliner.entities.SampleUniquenessReport;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class SampleUniquenessCalculator implements PrivacyMetricCalculator<SampleUniquenessReport> {

    @Inject
    @PersistenceUnit("data")
    EntityManager em;

    @ConfigProperty(name = "policyLiner.privacy-metric.sampling-limit", defaultValue = "1000000")
    int sampleSizeLimit;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public SampleUniquenessReport getPrivacyMetricReportOfTable(String viewName, List<String> columns, List<String> sensitiveAttributes) {
        String columnString = String.join(",", columns);
        String queryString = """
                WITH eq AS (
                    SELECT %s, COUNT(*) AS cnt
                    FROM (SELECT %s FROM %s
                    ORDER BY RANDOM()
                    LIMIT %d
                    ) AS sample
                    GROUP BY %s
                )
                SELECT
                    SUM(CASE WHEN cnt = 1 THEN 1 ELSE 0 END) AS uniqueRowCount,
                    SUM(cnt) AS totalRowCount,
                    SUM(CASE WHEN cnt = 1 THEN 1 ELSE 0 END) * 1.0 / SUM(cnt) AS uniquenessRatio
                FROM eq;
                """.formatted(columnString, columnString, viewName, sampleSizeLimit, columnString);

        Query query = em.createNativeQuery(queryString, SampleUniquenessReport.class);
        SampleUniquenessReport report = (SampleUniquenessReport) query.getSingleResult();
        report.setViewName(viewName);
        return report;
    }
}
