package de.tub.dima.policyliner.database.data.metrics.popuniqueness;

import de.tub.dima.policyliner.entities.PopulationUniquenessReport;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@ApplicationScoped
public class SNBEstimator {


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public PopulationUniquenessReport getSNBPopulationUniquenessReport(
            String viewName,
            long sampleSize,
            long populationSize,
            Map<Integer, Long> eqFrequencyMap) {

        final double samplingRate = (double) sampleSize / populationSize;
        try {
            NBParameters params = fitNegativeBinomial(eqFrequencyMap);
            if (!params.converged) {
                throw new RuntimeException("SNB parameters did not converge");
            }

            double estimatedUniqueness = predictPopulationUniqueness(params, sampleSize, populationSize);

            double lambda3Value = estimatedUniqueness / populationSize;
            lambda3Value = Math.max(0.0, Math.min(1.0, lambda3Value));

            BigDecimal estimatedPopulationUniquenessRatio = BigDecimal.valueOf(lambda3Value)
                    .setScale(6, RoundingMode.HALF_UP);

            return new PopulationUniquenessReport(
                    viewName,
                    estimatedPopulationUniquenessRatio,
                    "SNB",
                    samplingRate,
                    sampleSize,
                    populationSize,
                    eqFrequencyMap.values().stream().mapToLong(Long::longValue).sum()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate SNB population uniqueness", e);
        }
    }

    public boolean converges(Map<Integer, Long> eqFrequencyMap) {
        try {
            NBParameters test = fitNegativeBinomial(eqFrequencyMap);
            return test.converged;
        } catch (Exception e) {
            return false;
        }
    }

    private NBParameters fitNegativeBinomial(Map<Integer, Long> eqFrequencyMap) {
        // Calculate mean and variance of equivalence class sizes
        double mean = 0.0;
        double variance = 0.0;
        long totalClasses = 0;

        for (Map.Entry<Integer, Long> entry : eqFrequencyMap.entrySet()) {
            int size = entry.getKey();
            long count = entry.getValue();
            mean += size * count;
            totalClasses += count;
        }

        if (totalClasses == 0) {
            return new NBParameters(1.0, 0.5, false);
        }

        mean = mean / totalClasses;

        for (Map.Entry<Integer, Long> entry : eqFrequencyMap.entrySet()) {
            int size = entry.getKey();
            long count = entry.getValue();
            variance += count * Math.pow(size - mean, 2);
        }

        variance = variance / totalClasses;

        if (variance <= mean || mean == 0) {
            return new NBParameters(1.0, mean / (mean + 1), false);
        }

        double successProb = mean / variance;
        double shapeParam = (mean * mean) / (variance - mean);

        // Check for valid parameters
        boolean converged = successProb > 0 && successProb < 1 && shapeParam > 0 && !Double.isNaN(successProb) && !Double.isNaN(shapeParam);

        return new NBParameters(shapeParam, successProb, converged);
    }

    private double predictPopulationUniqueness(NBParameters params, long sampleSize, long populationSize) {
        // Expected number of classes of size 1 in population
        double scaleFactor = (double) populationSize / sampleSize;
        double adjustedProb = Math.pow(params.successProb, scaleFactor);

        return params.shapeParam * (1 - adjustedProb);
    }


    private record NBParameters(double shapeParam, double successProb, boolean converged) {
    }
}
