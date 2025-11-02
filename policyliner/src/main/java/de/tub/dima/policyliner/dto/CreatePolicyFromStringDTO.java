package de.tub.dima.policyliner.dto;

public class CreatePolicyFromStringDTO {
    private String policy;
    private Boolean isMaterializedView;
    private Boolean useDefaultMetrics;
    private Boolean evaluatePolicyUponCreation;

    public CreatePolicyFromStringDTO(String policy, Boolean isMaterializedView, Boolean useDefaultMetrics, Boolean evaluatePolicyUponCreation) {
        this.policy = policy;
        this.isMaterializedView = isMaterializedView;
        this.useDefaultMetrics = useDefaultMetrics;
        this.evaluatePolicyUponCreation = evaluatePolicyUponCreation;
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

    public Boolean getEvaluatePolicyUponCreation() {
        return evaluatePolicyUponCreation;
    }
    public void setEvaluatePolicyUponCreation(Boolean evaluatePolicyUponCreation) {
        this.evaluatePolicyUponCreation = evaluatePolicyUponCreation;
    }
}
