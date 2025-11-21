package de.tub.dima.policyliner.entities;

import java.math.BigDecimal;

public class PopulationUniquenessReport {
    private String viewName;
    private BigDecimal estimatedPopulationUniquenessRatio;
    private String estimatorUsed;
    private Double samplingRate;
    private Long sampleSize;
    private Long populationSize;
    private Long totalEquivalenceClasses;

    public PopulationUniquenessReport(
            String viewName,
            BigDecimal estimatedPopulationUniquenessRatio,
            String estimatorUsed,
            Double samplingRate,
            Long sampleSize,
            Long populationSize,
            Long totalEquivalenceClasses) {
        this.viewName = viewName;
        this.estimatedPopulationUniquenessRatio = estimatedPopulationUniquenessRatio;
        this.estimatorUsed = estimatorUsed;
        this.samplingRate = samplingRate;
        this.sampleSize = sampleSize;
        this.populationSize = populationSize;
        this.totalEquivalenceClasses = totalEquivalenceClasses;
    }

    public String getViewName() { return viewName; }
    public void setViewName(String viewName) { this.viewName = viewName; }

    public BigDecimal getEstimatedPopulationUniquenessRatio() { return estimatedPopulationUniquenessRatio; }
    public void setEstimatedPopulationUniquenessRatio(BigDecimal estimatedPopulationUniquenessRatio) { this.estimatedPopulationUniquenessRatio = estimatedPopulationUniquenessRatio; }

    public String getEstimatorUsed() { return estimatorUsed; }
    public void setEstimatorUsed(String estimatorUsed) { this.estimatorUsed = estimatorUsed; }

    public Double getSamplingRate() { return samplingRate; }
    public void setSamplingRate(Double samplingRate) { this.samplingRate = samplingRate; }

    public Long getSampleSize() { return sampleSize; }
    public void setSampleSize(Long sampleSize) { this.sampleSize = sampleSize; }

    public Long getPopulationSize() { return populationSize; }
    public void setPopulationSize(Long populationSize) { this.populationSize = populationSize; }

    public Long getTotalEquivalenceClasses() { return totalEquivalenceClasses; }
    public void setTotalEquivalenceClasses(Long totalEquivalenceClasses) {
        this.totalEquivalenceClasses = totalEquivalenceClasses;
    }

    @Override
    public String toString() {
        return """
                PopulationUniquenessReport{
                viewName= %s
                , estimatedPopulationUniquenessRatio= %.6f
                , estimatorUsed= %s
                , samplingRate= %.6f
                , sampleSize= %d
                , populationSize= %d
                , totalEquivalenceClasses= %d
                }
                """.formatted(
                viewName,
                estimatedPopulationUniquenessRatio != null ? estimatedPopulationUniquenessRatio.doubleValue() : 0.0,
                estimatorUsed,
                samplingRate != null ? samplingRate : 0.0,
                sampleSize,
                populationSize,
                totalEquivalenceClasses
        );
    }

}
