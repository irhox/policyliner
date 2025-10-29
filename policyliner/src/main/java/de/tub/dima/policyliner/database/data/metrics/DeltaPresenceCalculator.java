package de.tub.dima.policyliner.database.data.metrics;

import de.tub.dima.policyliner.entities.DeltaPresenceReport;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class DeltaPresenceCalculator {

    @Inject
    @PersistenceUnit("data")
    EntityManager em;

    @ConfigProperty(name = "policyLiner.delta-presence.sampling-rate", defaultValue = "0.2")
    double samplingRate;


    @ConfigProperty(name = "policyLiner.delta-presence.sampling-limit", defaultValue = "1000000")
    int sampleSizeLimit;

    public DeltaPresenceCalculator() {}

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public DeltaPresenceReport getDeltaPresenceReportOfTable(String viewName, List<String> columns) {
        String viewColumnString = String.join(", ", columns);

        String queryString = String.format(Locale.US, """
                WITH policy_sample AS (
                    SELECT %s FROM %s ORDER BY RANDOM() LIMIT %d
                ),
                eq_classes AS (
                    SELECT %s, COUNT(*) AS eq_size
                    FROM policy_sample
                    GROUP BY %s
                ),
                frequency_counts AS (
                    SELECT eq_size, COUNT(*) AS num_classes_with_size
                    FROM eq_classes
                    GROUP BY eq_size
                ),
                smoothed_frequencies AS (
                    SELECT %s,
                    eq.eq_size,
                    eq.eq_size::decimal AS sample_count,
                    SUM(eq.eq_size) OVER () AS total_sample,
                    (SUM(eq.eq_size) OVER () / %f) AS estimated_population,
                    CASE
                        WHEN eq.eq_size = 1 THEN
                            COALESCE(
                                (SELECT num_classes_with_size * 2.0 FROM frequency_counts WHERE eq_size = 2) /
                                NULLIF ((SELECT num_classes_with_size FROM frequency_counts WHERE eq_size = 1), 0),
                                1.0 ) / (SUM(eq.eq_size) OVER () / %f)
                        WHEN eq.eq_size = 2 THEN
                            COALESCE(
                                (SELECT num_classes_with_size * 3.0 FROM frequency_counts WHERE eq_size = 3) /
                                NULLIF ((SELECT num_classes_with_size FROM frequency_counts WHERE eq_size = 2), 0),
                                2.0) / (SUM(eq.eq_size) OVER () / %f)
                        ELSE
                            (eq.eq_size::decimal + 1.0) / ((SUM(eq.eq_size) OVER () / %f) + COUNT(*) OVER ())
                    END AS estimated_pop_frequency,
                    eq.eq_size::decimal / NULLIF(SUM(eq.eq_size) OVER (), 0) AS observed_frequency
                FROM eq_classes eq
                GROUP BY %s, eq.eq_size
                ),
                membership_inference AS (
                    SELECT
                        eq_size, sample_count, total_sample, estimated_population,
                        observed_frequency, estimated_pop_frequency, %f AS prior_in_dataset,
                        CASE
                            WHEN estimated_pop_frequency > 0 THEN
                                (%f * observed_frequency) /
                                NULLIF ((
                                    %f * observed_frequency + (1.0 - %f) * estimated_pop_frequency
                                ), 0)
                            ELSE 1.0
                        END AS posterior_in_dataset,
                        CASE
                            WHEN estimated_pop_frequency > 0 THEN
                                ABS(((%f * observed_frequency) /
                                NULLIF((%f * observed_frequency + (1.0 - %f) * estimated_pop_frequency), 0)) - %f)
                            ELSE ABS(1.0 - %f)
                        END AS delta_presence
                    FROM smoothed_frequencies
                ),
                risk_assessment AS (
                    SELECT
                        eq_size,
                        posterior_in_dataset,
                        delta_presence,
                        observed_frequency,
                        estimated_pop_frequency
                    FROM membership_inference
                )
                SELECT
                    MAX(delta_presence) AS maxDeltaPresence,
                    MIN(delta_presence) AS minDeltaPresence,
                    AVG(delta_presence) AS avgDeltaPresence,
                    COUNT(*) AS totalEquivalenceClasses,
                    MIN(eq_size) AS minEquivalenceClassSize,
                    MAX(eq_size) AS maxEquivalenceClassSize
                FROM risk_assessment
        """, viewColumnString, viewName, sampleSizeLimit,
                viewColumnString, viewColumnString,
                addPrefixToColumns(columns, "eq"),
                samplingRate, samplingRate, samplingRate, samplingRate,
                addPrefixToColumns(columns, "eq"),
                samplingRate,
                samplingRate, samplingRate, samplingRate,
                samplingRate, samplingRate, samplingRate, samplingRate, samplingRate);




        Query query = em.createNativeQuery(queryString);
        Object[] result = (Object[]) query.getSingleResult();

        if (result == null || result[0] == null) {
            return new DeltaPresenceReport(viewName, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L, 0L, 0L);
        }

        return new DeltaPresenceReport(
                viewName,
                new BigDecimal(result[0].toString()),
                new BigDecimal(result[1].toString()),
                new BigDecimal(result[2].toString()),
                ((Number) result[3]).longValue(),
                ((Number) result[4]).longValue(),
                ((Number) result[5]).longValue()
        );



    }

    private String addPrefixToColumns(List<String> columnList, String prefix) {
        return String.join(", ", columnList.stream().map(col -> prefix + "." + col).toList());
    }
}
