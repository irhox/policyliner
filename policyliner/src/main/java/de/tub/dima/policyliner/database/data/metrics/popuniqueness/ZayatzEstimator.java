package de.tub.dima.policyliner.database.data.metrics.popuniqueness;

import de.tub.dima.policyliner.entities.PopulationUniquenessReport;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@ApplicationScoped
public class ZayatzEstimator {


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public PopulationUniquenessReport getZayatzPopulationUniquenessReport(
            String viewName,
            long sampleSize,
            long populationSize,
            Map<Integer, Long> eqFrequencyMap) {

        final double samplingRate = (double) sampleSize / populationSize;

        double estimatedUniqueness = 0.0;
        for (Map.Entry<Integer, Long> entry : eqFrequencyMap.entrySet()) {
            int classSize = entry.getKey();
            long frequency = entry.getValue();

            double uniqueProbability = 1.0 / classSize;

            estimatedUniqueness += frequency * classSize * uniqueProbability * (1.0 / samplingRate);
        }

        double lambda3Value = estimatedUniqueness / populationSize;

        lambda3Value = Math.max(0.0, Math.min(1.0, lambda3Value));

        BigDecimal estimatedPopulationUniquenessRatio = BigDecimal.valueOf(lambda3Value)
                .setScale(6, RoundingMode.HALF_UP);

        return new PopulationUniquenessReport(
                viewName,
                estimatedPopulationUniquenessRatio,
                "Zayatz",
                samplingRate,
                sampleSize,
                populationSize,
                eqFrequencyMap.values().stream().mapToLong(Long::longValue).sum()
        );
    }
}
