package de.tub.dima.policyliner.dto;

public class CreatePolicyFromStringDTO {
    private String policy;
    private Boolean isMaterializedView;
    private Boolean useDefaultMetrics;

    public CreatePolicyFromStringDTO(String policy, Boolean isMaterializedView, Boolean useDefaultMetrics) {
        this.policy = policy;
        this.isMaterializedView = isMaterializedView;
        this.useDefaultMetrics = useDefaultMetrics;
    }


    public String getPolicy() {
        return policy;
    }
    public Boolean getIsMaterializedView() {
        return isMaterializedView;
    }
    public void setIsMaterializedView(Boolean isMaterializedView) {
        this.isMaterializedView = isMaterializedView;
    }
    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public Boolean getUseDefaultMetrics() {
        return useDefaultMetrics;
    }
    public void setUseDefaultMetrics(Boolean useDefaultMetrics) {
        this.useDefaultMetrics = useDefaultMetrics;
    }
}
