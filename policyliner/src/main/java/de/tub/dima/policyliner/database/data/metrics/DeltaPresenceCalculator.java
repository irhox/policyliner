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
public class DeltaPresenceCalculator implements PrivacyMetricCalculator<DeltaPresenceReport> {

    @Inject
    @PersistenceUnit("data")
    EntityManager em;

    @ConfigProperty(name = "policyLiner.delta-presence.sampling-rate", defaultValue = "0.2")
    double samplingRate;


    @ConfigProperty(name = "policyLiner.privacy-metric.sampling-limit", defaultValue = "1000000")
    int sampleSizeLimit;

    public DeltaPresenceCalculator() {}

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public DeltaPresenceReport getPrivacyMetricReportOfTable(String viewName, List<String> columns, List<String> sensitiveAttributes) {
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
                    eq.eq_size AS eq_size,
                    SUM(eq.eq_size) OVER () AS total_sample,
                    CASE
                        WHEN eq.eq_size = 1 THEN
                            COALESCE(
                            2.0 * (SELECT num_classes_with_size FROM frequency_counts WHERE eq_size = 2) /
                            NULLIF ((SELECT num_classes_with_size FROM frequency_counts WHERE eq_size = 1), 0), 1.0)
                        WHEN eq.eq_size = 2 THEN
                            COALESCE(
                            3.0 * (SELECT num_classes_with_size FROM frequency_counts WHERE eq_size = 3) /
                            NULLIF ((SELECT num_classes_with_size FROM frequency_counts WHERE eq_size = 2), 0), 2.0)
                        ELSE
                            COALESCE(
                            (eq.eq_size + 1.0) * (SELECT num_classes_with_size FROM frequency_counts WHERE eq_size = eq.eq_size + 1) /
                            NULLIF((SELECT num_classes_with_size FROM frequency_counts WHERE eq_size = eq.eq_size), 0), eq.eq_size::decimal)
                    END AS adjusted_count
                FROM eq_classes eq
                GROUP BY %s, eq.eq_size
                ),
                sample_frequencies AS (
                    SELECT *, eq_size::decimal / NULLIF(total_sample, 0) AS observed_sample_frequency,
                    adjusted_count / NULLIF(total_sample, 0) AS adjusted_sample_frequency
                    FROM smoothed_frequencies
                ),
                population_frequencies AS (
                    SELECT *, %f AS sampling_rate,
                    total_sample / %f AS estimated_population,
                    observed_sample_frequency / %f AS observed_pop_frequency,
                    adjusted_sample_frequency / %f AS estimated_pop_frequency
                    FROM sample_frequencies
                ),
                membership_inference AS (
                    SELECT
                        eq_size, adjusted_count, total_sample, estimated_population,
                        observed_sample_frequency, adjusted_sample_frequency,
                        observed_pop_frequency, estimated_pop_frequency,
                        %f AS prior_in_dataset,
                        CASE
                            WHEN estimated_pop_frequency > 0 THEN
                                (%f * observed_sample_frequency) /
                                NULLIF ((
                                    %f * observed_sample_frequency + (1.0 - %f) * estimated_pop_frequency
                                ), 0)
                            ELSE 1.0
                        END AS posterior_in_dataset,
                        CASE
                            WHEN estimated_pop_frequency > 0 THEN
                                ABS(((%f * observed_sample_frequency) /
                                NULLIF((%f * observed_sample_frequency + (1.0 - %f) * estimated_pop_frequency), 0)) - %f)
                            ELSE ABS(1.0 - %f)
                        END AS delta_presence
                    FROM population_frequencies
                ),
                risk_assessment AS (
                    SELECT
                        eq_size,
                        posterior_in_dataset,
                        delta_presence,
                        observed_sample_frequency,
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
                addPrefixToColumns(columns, "eq"),
                samplingRate, samplingRate, samplingRate, samplingRate,
                samplingRate, samplingRate, samplingRate, samplingRate,
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
