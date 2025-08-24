package de.tub.dima.policyliner.dto;

public class CreatePolicyFromStringDTO {
    private String policy;
    private Boolean isMaterializedView;

    public CreatePolicyFromStringDTO(String policy, Boolean isMaterializedView) {
        this.policy = policy;
        this.isMaterializedView = isMaterializedView;
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
}
