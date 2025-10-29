package de.tub.dima.policyliner.database.data.metrics;

import de.tub.dima.policyliner.entities.SampleUniquenessReport;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class UniquenessEstimationCalculator {

    @Inject
    @PersistenceUnit("data")
    EntityManager em;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public SampleUniquenessReport getSampleUniquenessReportOfTable(String viewName, List<String> columns) {
        String columnString = String.join(",", columns);
        String queryString = """
                WITH eq AS (
                    SELECT %s, COUNT(*) AS cnt
                    FROM (SELECT %s FROM %s
                    ORDER BY RANDOM()
                    LIMIT 1000000
                    ) AS sample
                    GROUP BY %s
                )
                SELECT
                    SUM(CASE WHEN cnt = 1 THEN 1 ELSE 0 END) AS uniqueRowCount,
                    SUM(cnt) AS totalRowCount,
                    SUM(CASE WHEN cnt = 1 THEN 1 ELSE 0 END) * 1.0 / SUM(cnt) AS uniquenessRatio
                FROM eq;
                """.formatted(columnString, columnString, viewName, columnString);

        Query query = em.createNativeQuery(queryString, SampleUniquenessReport.class);
        return (SampleUniquenessReport) query.getSingleResult();
    }
}
