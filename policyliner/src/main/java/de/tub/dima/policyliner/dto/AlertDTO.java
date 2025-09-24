package de.tub.dima.policyliner.dto;

import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;

import java.time.LocalDateTime;

public class AlertDTO {
    private String id;
    private String message;
    private AlertSeverity severity;
    private AlertType type;
    private Boolean isResolved;
    private LocalDateTime createdAt;

    public AlertDTO(String id, String message, AlertSeverity severity, AlertType type, Boolean isResolved, LocalDateTime createdAt) {
        this.id = id;
        this.message = message;
        this.severity = severity;
        this.type = type;
        this.isResolved = isResolved;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(Boolean isResolved) {
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
