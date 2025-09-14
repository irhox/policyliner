package de.tub.dima.policyliner.dto;

import java.util.List;

public class CreatePolicyDTO {
    private String policyName;
    private List<TableInfoDTO> tables;
    private List<ViewAttributeDTO> columns;
    private Boolean isMaterializedView;
    private String userRole;

    public CreatePolicyDTO(
            String policyName,
            List<TableInfoDTO> tables,
            List<ViewAttributeDTO> columns,
            Boolean isMaterializedView,
            String userRole) {
        this.policyName = policyName;
        this.tables = tables;
        this.columns = columns;
        this.isMaterializedView = isMaterializedView;
        this.userRole = userRole;
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

    public Boolean getIsMaterializedView() {
        return isMaterializedView;
    }
    public void setIsMaterializedView(Boolean isMaterializedView) {
        this.isMaterializedView = isMaterializedView;
    }

    public String getUserRole() {
        return userRole;
    }
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "CreatePolicyDTO{" +
                "policyName=" + policyName +
                ", tables=" + tables +
                ", columns=" + columns +
                ", isMaterializedView=" + isMaterializedView +
                ", userRole=" + userRole + '}';
    }
}
