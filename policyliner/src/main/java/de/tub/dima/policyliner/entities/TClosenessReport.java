package de.tub.dima.policyliner.entities;

import java.math.BigDecimal;
import java.util.Map;

public class TClosenessReport {
    private String viewName;
    private String sensitiveAttribute;
    private BigDecimal maxDistance;
    private BigDecimal avgDistance;
    private Long totalEquivalenceClasses;
    private Long violatingEquivalenceClasses;
    private Map<String, BigDecimal> equivalenceClassDistances;

    public TClosenessReport(String viewName, String sensitiveAttribute, BigDecimal maxDistance, BigDecimal avgDistance, Long totalEquivalenceClasses, Long violatingEquivalenceClasses, Map<String, BigDecimal> equivalenceClassDistances) {
        this.viewName = viewName;
        this.sensitiveAttribute = sensitiveAttribute;
        this.maxDistance = maxDistance;
        this.avgDistance = avgDistance;
        this.totalEquivalenceClasses = totalEquivalenceClasses;
        this.violatingEquivalenceClasses = violatingEquivalenceClasses;
        this.equivalenceClassDistances = equivalenceClassDistances;
    }

    public String getViewName() {
        return viewName;
    }
    public String getSensitiveAttribute() {
        return sensitiveAttribute;
    }
    public BigDecimal getMaxDistance() {
        return maxDistance;
    }
    public BigDecimal getAvgDistance() {
        return avgDistance;
    }
    public Long getTotalEquivalenceClasses() {
        return totalEquivalenceClasses;
    }
    public Long getViolatingEquivalenceClasses() {
        return violatingEquivalenceClasses;
    }
    public Map<String, BigDecimal> getEquivalenceClassDistances() {
        return equivalenceClassDistances;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    public void setSensitiveAttribute(String sensitiveAttribute) {
        this.sensitiveAttribute = sensitiveAttribute;
    }
    public void setMaxDistance(BigDecimal maxDistance) {
        this.maxDistance = maxDistance;
    }
    public void setAvgDistance(BigDecimal avgDistance) {
        this.avgDistance = avgDistance;
    }
    public void setTotalEquivalenceClasses(Long totalEquivalenceClasses) {
        this.totalEquivalenceClasses = totalEquivalenceClasses;
    }
    public void setViolatingEquivalenceClasses(Long violatingEquivalenceClasses) {
        this.violatingEquivalenceClasses = violatingEquivalenceClasses;
    }
    public void setEquivalenceClassDistances(Map<String, BigDecimal> equivalenceClassDistances) {
        this.equivalenceClassDistances = equivalenceClassDistances;
    }


    @Override
    public String toString() {
        return """
                TClosenessReport{
                viewName= %s
                , sensitiveAttribute= %s
                , maxDistance= %.4f
                , averageDistance= %.4f
                , totalEquivalenceClasses= %d
                , violatingEquivalenceClasses= %d
                }
                """.formatted(viewName, sensitiveAttribute,
                maxDistance != null ? maxDistance.doubleValue() : 0.0,
                avgDistance != null ? avgDistance.doubleValue() : 0.0,
                totalEquivalenceClasses, violatingEquivalenceClasses);
    }


}
