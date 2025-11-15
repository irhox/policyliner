package de.tub.dima.policyliner.database.data.metrics.popuniqueness;

import de.tub.dima.policyliner.database.data.metrics.PrivacyMetricCalculator;
import de.tub.dima.policyliner.entities.PopulationUniquenessReport;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PopulationUniquenessEstimationCalculator implements PrivacyMetricCalculator<PopulationUniquenessReport> {

    @Inject
    @PersistenceUnit("data")
    EntityManager em;

    @Inject
    PitmanEstimator pitmanEstimator;

    @Inject
    SNBEstimator snbEstimator;

    @Inject
    ZayatzEstimator zayatzEstimator;

    @ConfigProperty(name = "policyLiner.privacy-metric.population-size", defaultValue = "10000000")
    int populationSize;

    @ConfigProperty(name = "policyLiner.privacy-metric.sampling-limit", defaultValue = "1000000")
    int sampleSizeLimit;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public PopulationUniquenessReport getPrivacyMetricReportOfTable(String viewName, List<String> columns, List<String> sensitiveAttributes) {
        Map<Integer, Long> eqFrequencyMap = getFrequencyOfEquivalenceClasses(viewName, columns);
        long sampleSize = getSampleSize(viewName);
        double samplingRate = (double) sampleSize / populationSize;

        // Decision rule from Dankar et al. (2012)
        if (samplingRate < 0.1) {
            return pitmanEstimator.getPitmanPopulationUniquenessReport(viewName, sampleSize, populationSize, eqFrequencyMap);
        } else {
            if (snbEstimator.converges(eqFrequencyMap)) {
                PopulationUniquenessReport snbResult = snbEstimator.getSNBPopulationUniquenessReport(viewName, sampleSize, populationSize, eqFrequencyMap);
                PopulationUniquenessReport zayatzResult = zayatzEstimator.getZayatzPopulationUniquenessReport(viewName, sampleSize, populationSize, eqFrequencyMap);

                if (zayatzResult.getEstimatedPopulationUniquenessRatio().compareTo(snbResult.getEstimatedPopulationUniquenessRatio()) > 0) {
                    return zayatzResult;
                } else {
                    return snbResult;
                }
            } else {
                return zayatzEstimator.getZayatzPopulationUniquenessReport(viewName, sampleSize, populationSize, eqFrequencyMap);
            }
        }
    }

    private Map<Integer, Long> getFrequencyOfEquivalenceClasses(String viewName, List<String> columns) {
        String columnString = String.join(",", columns);

        String queryString = """
                SELECT class_size, COUNT(*) as frequency
                FROM (
                    SELECT %s, COUNT(*) as class_size
                    FROM %s
                    GROUP BY %s
                    LIMIT %d
                ) AS equivalence_classes
                GROUP BY class_size
                ORDER BY class_size
                """.formatted(columnString, viewName, columnString, sampleSizeLimit);

        Query query = em.createNativeQuery(queryString);
        List<Object[]> results = query.getResultList();

        Map<Integer, Long> eqFrequencyMap = new HashMap<>();
        for (Object[] row : results) {
            Integer classSize = ((Number) row[0]).intValue();
            Long frequency = ((Number) row[1]).longValue();
            eqFrequencyMap.put(classSize, frequency);
        }

        return eqFrequencyMap;
    }

    private long getSampleSize(String viewName) {
        String queryString = "SELECT COUNT(*) FROM %s LIMIT %d"
                .formatted(viewName, sampleSizeLimit);

        Query query = em.createNativeQuery(queryString);
        Number result = (Number) query.getSingleResult();
        return Math.min(result.longValue(), sampleSizeLimit);
    }
}
