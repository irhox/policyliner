package de.tub.dima.policyliner.dto;

import de.tub.dima.policyliner.entities.JsonQuasiIdentifier;

import java.util.List;

public class CreatePolicyDTO {
    private String policyName;
    private List<TableInfoDTO> tables;
    private List<ViewAttributeDTO> columns;
    private Boolean useStaticMasking;
    private String userRole;
    private Boolean useDefaultMetrics;
    private Boolean evaluatePolicyUponCreation;
    private JsonQuasiIdentifier quasiIdentifier;

    public CreatePolicyDTO(
            String policyName,
            List<TableInfoDTO> tables,
            List<ViewAttributeDTO> columns,
            Boolean useStaticMasking,
            String userRole,
            Boolean useDefaultMetrics,
            Boolean evaluatePolicyUponCreation,
            JsonQuasiIdentifier quasiIdentifier) {
        this.policyName = policyName;
        this.tables = tables;
        this.columns = columns;
        this.useStaticMasking = useStaticMasking;
        this.userRole = userRole;
        this.useDefaultMetrics = useDefaultMetrics;
        this.evaluatePolicyUponCreation = evaluatePolicyUponCreation;
        this.quasiIdentifier = quasiIdentifier;
    }

    public CreatePolicyDTO() {
    }

    public String getPolicyName() {
        return policyName;
    }

    public List<TableInfoDTO> getTables() {
        return tables;
    }

    public List<ViewAttributeDTO> getColumns() {
        return columns;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public void setTables(List<TableInfoDTO> tables) {
        this.tables = tables;
    }

    public void setColumns(List<ViewAttributeDTO> columns) {
        this.columns = columns;
    }

    public Boolean getUseStaticMasking() {
        return useStaticMasking;
    }
    public void setUseStaticMasking(Boolean useStaticMasking) {
        this.useStaticMasking = useStaticMasking;
    }

    public String getUserRole() {
        return userRole;
    }
    public void setUserRole(String userRole) {
        this.userRole = userRole;
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

    @Override
    public String toString() {
        return "CreatePolicyDTO{" +
                "policyName=" + policyName +
                ", tables=" + tables +
                ", columns=" + columns +
                ", useStaticMasking=" + useStaticMasking +
                ", userRole=" + userRole + '}';
    }
}
