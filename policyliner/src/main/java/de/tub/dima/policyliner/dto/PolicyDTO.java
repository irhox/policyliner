package de.tub.dima.policyliner.dto;

import de.tub.dima.policyliner.constants.PolicyStatus;

import java.time.LocalDateTime;

public class PolicyDTO {
    private String id;
    private String policy;
    private PolicyStatus status;
    private LocalDateTime deactivatedAt;

    public PolicyDTO(String id, String policy, PolicyStatus status, LocalDateTime deactivatedAt) {
        this.id = id;
        this.policy = policy;
        this.status = status;
        this.deactivatedAt = deactivatedAt;
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
}
