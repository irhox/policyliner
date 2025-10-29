package de.tub.dima.policyliner.entities;

import java.math.BigDecimal;

public class DeltaPresenceReport {
    private String viewName;
    private BigDecimal maxDeltaPresence;
    private BigDecimal minDeltaPresence;
    private BigDecimal avgDeltaPresence;
    private Long totalEquivalenceClasses;
    private Long minEquivalenceClassSize;
    private Long maxEquivalenceClassSize;

    public DeltaPresenceReport() {}

    public DeltaPresenceReport(String viewName, BigDecimal maxDeltaPresence,
                               BigDecimal minDeltaPresence,
                               BigDecimal avgDeltaPresence, Long totalEquivalenceClasses,
                               Long minEquivalenceClassSize, Long maxEquivalenceClassSize) {
        this.viewName = viewName;
        this.maxDeltaPresence = maxDeltaPresence;
        this.minDeltaPresence = minDeltaPresence;
        this.avgDeltaPresence = avgDeltaPresence;
        this.totalEquivalenceClasses = totalEquivalenceClasses;
        this.minEquivalenceClassSize = minEquivalenceClassSize;
        this.maxEquivalenceClassSize = maxEquivalenceClassSize;
    }

    // Getters and Setters
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public BigDecimal getMaxDeltaPresence() {
        return maxDeltaPresence;
    }

    public void setMaxDeltaPresence(BigDecimal maxDeltaPresence) {
        this.maxDeltaPresence = maxDeltaPresence;
    }

    public BigDecimal getMinDeltaPresence() {
        return minDeltaPresence;
    }

    public void setMinDeltaPresence(BigDecimal minDeltaPresence) {
        this.minDeltaPresence = minDeltaPresence;
    }

    public BigDecimal getAvgDeltaPresence() {
        return avgDeltaPresence;
    }

    public void setAvgDeltaPresence(BigDecimal avgDeltaPresence) {
        this.avgDeltaPresence = avgDeltaPresence;
    }

    public Long getTotalEquivalenceClasses() {
        return totalEquivalenceClasses;
    }

    public void setTotalEquivalenceClasses(Long totalEquivalenceClasses) {
        this.totalEquivalenceClasses = totalEquivalenceClasses;
    }

    public Long getMinEquivalenceClassSize() {
        return minEquivalenceClassSize;
    }

    public void setMinEquivalenceClassSize(Long minEquivalenceClassSize) {
        this.minEquivalenceClassSize = minEquivalenceClassSize;
    }

    public Long getMaxEquivalenceClassSize() {
        return maxEquivalenceClassSize;
    }

    public void setMaxEquivalenceClassSize(Long maxEquivalenceClassSize) {
        this.maxEquivalenceClassSize = maxEquivalenceClassSize;
    }

    @Override
    public String toString() {
        return """
                DeltaPresenceReport{
                viewName= %s
                , maxDeltaPresence= %.4f
                , minDeltaPresence= %.4f
                , avgDeltaPresence= %.4f
                , totalEquivalenceClasses= %d
                , minEquivalenceClassSize= %d
                , maxEquivalenceClassSize= %d
                }
                """.formatted(viewName,
                maxDeltaPresence,
                minDeltaPresence,
                avgDeltaPresence,
                totalEquivalenceClasses,
                minEquivalenceClassSize,
                maxEquivalenceClassSize);
    }
}
