package de.tub.dima.policyliner.dto;

import de.tub.dima.policyliner.constants.MetricSeverity;

public class PrivacyMetricDTO {
    private String id;
    private String name;
    private String description;
    private String value;
    private String valueType;
    private MetricSeverity metricSeverity;
    private String policyId;

    public PrivacyMetricDTO(String id, String name, String description, String value, String valueType, MetricSeverity metricSeverity, String policyId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
        this.valueType = valueType;
        this.metricSeverity = metricSeverity;
        this.policyId = policyId;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValueType() {
        return valueType;
    }
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
    public MetricSeverity getMetricSeverity() {
        return metricSeverity;
    }
    public void setMetricSeverity(MetricSeverity metricSeverity) {
        this.metricSeverity = metricSeverity;
    }
    public String getPolicyId() {
        return policyId;
    }
    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

}
