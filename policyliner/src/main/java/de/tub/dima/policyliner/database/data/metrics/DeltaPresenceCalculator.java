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

    @ConfigProperty(name = "policyLiner.delta-presence.prior-prob", defaultValue = "0.01")
    double priorProbability;


    @ConfigProperty(name = "policyLiner.privacy-metric.sampling-limit", defaultValue = "10000000")
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
                    SELECT eq_size AS r, COUNT(*) AS n_r
                    FROM eq_classes
                    GROUP BY eq_size
                ),
                sgt_smoothed AS (
                    SELECT
                        fc.r,
                        fc.n_r,
                        CASE
                            WHEN fc.r = 1 THEN
                                COALESCE(
                                    2.0 * (SELECT n_r FROM frequency_counts WHERE r = 2) /
                                    NULLIF(fc.n_r, 0),
                                    0.5
                                )
                            WHEN fc.r = 2 THEN
                                COALESCE(
                                    3.0 * (SELECT n_r FROM frequency_counts WHERE r = 3) /
                                    NULLIF(fc.n_r, 0),
                                    1.5
                                )
                            WHEN fc.r <= 10 THEN
                                COALESCE(
                                    (fc.r + 1.0) * (SELECT n_r FROM frequency_counts WHERE r = fc.r + 1) /
                                    NULLIF(fc.n_r, 0),
                                    fc.r * (1.0 - 2.0/(fc.r + 1.0))
                                )
                            ELSE
                                fc.r * (fc.r - 1.0) / (fc.r + 1.0)
                        END AS r_star
                    FROM frequency_counts fc
                ),
                sample_totals AS (
                    SELECT SUM(eq_size) AS total_sample
                    FROM eq_classes
                ),
                eq_classes_with_smoothing AS (
                    SELECT
                        %s,
                        eq.eq_size AS r,
                        COALESCE(sgt.r_star, eq.eq_size::decimal) AS r_star,
                        st.total_sample,
                        eq.eq_size::decimal / NULLIF(st.total_sample, 0) AS p_sample_raw,
                        COALESCE(sgt.r_star, eq.eq_size::decimal) / NULLIF(st.total_sample, 0) AS p_sample_adjusted
                    FROM eq_classes eq
                    LEFT JOIN sgt_smoothed sgt ON eq.eq_size = sgt.r
                    CROSS JOIN sample_totals st
                ),
                membership_inference AS (
                    SELECT
                        r AS eq_size,
                        r_star AS adjusted_count,
                        total_sample,
                        %f AS prior_in_dataset,
                        p_sample_raw AS observed_sample_frequency,
                        p_sample_adjusted AS estimated_pop_frequency,
                        CASE
                            WHEN p_sample_adjusted > 0 THEN
                                (%f * p_sample_raw) /
                                NULLIF(
                                    %f * p_sample_raw + (1.0 - %f) * p_sample_adjusted,
                                    0
                                )
                            ELSE %f
                        END AS posterior_in_dataset,
                        CASE
                            WHEN p_sample_adjusted > 0 THEN
                                ABS(
                                    ((%f * p_sample_raw) /
                                    NULLIF(
                                        %f * p_sample_raw + (1.0 - %f) * p_sample_adjusted,
                                        0
                                    )) - %f
                                )
                            ELSE 0.0
                        END AS delta_presence
                    FROM eq_classes_with_smoothing
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
                priorProbability,
                priorProbability, priorProbability, priorProbability, priorProbability,
                priorProbability, priorProbability, priorProbability, priorProbability);



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
