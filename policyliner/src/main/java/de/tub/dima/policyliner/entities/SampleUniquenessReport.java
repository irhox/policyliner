package de.tub.dima.policyliner.entities;

import java.math.BigDecimal;

public class SampleUniquenessReport {
    private String viewName;
    private Long uniqueRowCount;
    private BigDecimal totalRowCount;
    private BigDecimal uniquenessRatio;

    public SampleUniquenessReport(Long uniqueRowCount, BigDecimal totalRowCount, BigDecimal uniquenessRatio) {
        this.uniqueRowCount = uniqueRowCount;
        this.totalRowCount = totalRowCount;
        this.uniquenessRatio = uniquenessRatio;
    }

    public SampleUniquenessReport(String viewName, Long uniqueRowCount, BigDecimal totalRowCount, BigDecimal uniquenessRatio) {
        this.viewName = viewName;
        this.uniqueRowCount = uniqueRowCount;
        this.totalRowCount = totalRowCount;
        this.uniquenessRatio = uniquenessRatio;
    }

    public String getViewName() {
        return viewName;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Long getUniqueRowCount() {
        return uniqueRowCount;
    }
    public BigDecimal getTotalRowCount() {
        return totalRowCount;
    }
    public BigDecimal getUniquenessRatio() {
        return uniquenessRatio;
    }

    public void setUniqueRowCount(Long uniqueRowCount) {
        this.uniqueRowCount = uniqueRowCount;
    }

    public void setTotalRowCount(BigDecimal totalRowCount) {
        this.totalRowCount = totalRowCount;
    }

    public void setUniquenessRatio(BigDecimal uniquenessRatio) {
        this.uniquenessRatio = uniquenessRatio;
    }

    @Override
    public String toString() {
        return """
                SampleUniquenessReport{
                viewName= %s
                , uniqueRowCount= %s
                , totalRowCount= %s
                , uniquenessRatio= %.3f
                }
                """.formatted(viewName, uniqueRowCount, totalRowCount, uniquenessRatio.doubleValue());
    }
}
