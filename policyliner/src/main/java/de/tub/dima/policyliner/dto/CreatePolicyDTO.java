package de.tub.dima.policyliner.dto;

import java.util.List;

public class CreatePolicyDTO {
    private String policyName;
    private String tableName;
    private List<ViewAttributeDTO> columns;

    public CreatePolicyDTO(String policyName, String tableName, List<ViewAttributeDTO> columns) {
        this.policyName = policyName;
        this.tableName = tableName;
        this.columns = columns;
    }

    public CreatePolicyDTO() {
    }

    public String getPolicyName() {
        return policyName;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ViewAttributeDTO> getColumns() {
        return columns;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumns(List<ViewAttributeDTO> columns) {
        this.columns = columns;
    }
}
