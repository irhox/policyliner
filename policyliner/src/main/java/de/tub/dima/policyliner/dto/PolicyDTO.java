package de.tub.dima.policyliner.dto;

import de.tub.dima.policyliner.constants.PolicyStatus;

import java.time.LocalDateTime;
import java.util.List;

public class PolicyDTO {
    private String id;
    private String policy;
    private PolicyStatus status;
    private LocalDateTime createdAt;
    private String allowedUserRole;
    private String viewName;
    private String materializedViewName;
    private LocalDateTime deactivatedAt;
    private List<String> alertIdList;

    public PolicyDTO(
            String id,
            String policy,
            PolicyStatus status,
            LocalDateTime createdAt,
            String allowedUserRole,
            String viewName,
            String materializedViewName,
            LocalDateTime deactivatedAt,
            List<String> alertIdList
            ) {
        this.id = id;
        this.policy = policy;
        this.status = status;
        this.createdAt = createdAt;
        this.allowedUserRole = allowedUserRole;
        this.viewName = viewName;
        this.materializedViewName = materializedViewName;
        this.deactivatedAt = deactivatedAt;
        this.alertIdList = alertIdList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(LocalDateTime deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAllowedUserRole() {
        return allowedUserRole;
    }

    public void setAllowedUserRole(String allowedUserRole) {
        this.allowedUserRole = allowedUserRole;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getMaterializedViewName() {
        return materializedViewName;
    }

    public void setMaterializedViewName(String materializedViewName) {
        this.materializedViewName = materializedViewName;
    }

    public List<String> getAlertIdList() {
        return alertIdList;
    }

    public void setAlertIdList(List<String> alertIdList) {
        this.alertIdList = alertIdList;
    }
}
