package de.tub.dima.policyliner.dto;

import de.tub.dima.policyliner.entities.JsonQuasiIdentifier;

public class CreatePolicyFromStringDTO {
    private String policy;
    private Boolean useStaticMasking;
    private Boolean useDefaultMetrics;
    private Boolean evaluatePolicyUponCreation;
    private JsonQuasiIdentifier quasiIdentifier;

    public CreatePolicyFromStringDTO(String policy, Boolean useStaticMasking, Boolean useDefaultMetrics, Boolean evaluatePolicyUponCreation, JsonQuasiIdentifier quasiIdentifier) {
        this.policy = policy;
        this.useStaticMasking = useStaticMasking;
        this.useDefaultMetrics = useDefaultMetrics;
        this.evaluatePolicyUponCreation = evaluatePolicyUponCreation;
        this.quasiIdentifier = quasiIdentifier;
    }


    public String getPolicy() {
        return policy;
    }
    public Boolean getUseStaticMasking() {
        return useStaticMasking;
    }
    public void setUseStaticMasking(Boolean useStaticMasking) {
        this.useStaticMasking = useStaticMasking;
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
    public JsonQuasiIdentifier getQuasiIdentifier() {
        return quasiIdentifier;
    }
    public void setQuasiIdentifier(JsonQuasiIdentifier quasiIdentifier) {
        this.quasiIdentifier = quasiIdentifier;
    }
}
