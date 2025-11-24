package de.tub.dima.policyliner.database.data.metrics.popuniqueness;

import de.tub.dima.policyliner.entities.PopulationUniquenessReport;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@ApplicationScoped
public class PitmanEstimator {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public PopulationUniquenessReport getPitmanPopulationUniquenessReport(
            String viewName,
            long sampleSize,
            long populationSize,
            Map<Integer, Long> eqFrequencyMap) {

        final double samplingRate = (double) sampleSize / populationSize;

        long f1 = eqFrequencyMap.getOrDefault(1, 0L);
        long f2 = eqFrequencyMap.getOrDefault(2, 0L);

        double adjustmentFactor = f2 > 0 ? (2.0 * f2) / Math.max(1, f1) : 1.0;

        double estimatedUniqueness = f1 * adjustmentFactor * (1.0 / samplingRate);

        double lambda3Value = estimatedUniqueness / populationSize;
        lambda3Value = Math.max(0.0, Math.min(1.0, lambda3Value));

        BigDecimal estimatedPopulationUniquenessRatio = BigDecimal.valueOf(lambda3Value)
                .setScale(6, RoundingMode.HALF_UP);

        return new PopulationUniquenessReport(
                viewName,
                estimatedPopulationUniquenessRatio,
                "Pitman",
                samplingRate,
                sampleSize,
                populationSize,
                eqFrequencyMap.values().stream().mapToLong(Long::longValue).sum()
        );
    }
}
