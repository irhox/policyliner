package de.tub.dima.policyliner.dto;

import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.QueryInspectionStatus;
import de.tub.dima.policyliner.constants.QueryStatus;

import java.time.LocalDateTime;
import java.util.Map;

public class QueryResponseDTO {
    private String id;
    private String userId;
    private String query;
    private QueryStatus status;
    private QueryInspectionStatus inspectionStatus;
    private String message;
    private LocalDateTime createdAt;
    private Map<String, AlertSeverity> alertSeverityMap;

    public QueryResponseDTO(String id, String userId, String query, QueryStatus status, QueryInspectionStatus inspectionStatus, String message, Map<String, AlertSeverity> alertSeverityMap, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.query = query;
        this.status = status;
        this.inspectionStatus = inspectionStatus;
        this.message = message;
        this.alertSeverityMap = alertSeverityMap;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public QueryStatus getStatus() {
        return status;
    }

    public void setStatus(QueryStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, AlertSeverity> getAlertSeverityMap() {
        return alertSeverityMap;
    }

    public void setAlertSeverityMap(Map<String, AlertSeverity> alertSeverityMap) {
        this.alertSeverityMap = alertSeverityMap;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public QueryInspectionStatus getInspectionStatus() {
        return inspectionStatus;
    }
    public void setInspectionStatus(QueryInspectionStatus inspectionStatus) {
        this.inspectionStatus = inspectionStatus;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
