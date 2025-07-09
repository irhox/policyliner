package de.tub.dima.policyliner.dto;

import java.util.List;

public class CreatePolicyDTO {
    private String policyName;
    private List<TableInfoDTO> tables;
    private List<ViewAttributeDTO> columns;

    public CreatePolicyDTO(String policyName, List<TableInfoDTO> tables, List<ViewAttributeDTO> columns) {
        this.policyName = policyName;
        this.tables = tables;
        this.columns = columns;
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
}
