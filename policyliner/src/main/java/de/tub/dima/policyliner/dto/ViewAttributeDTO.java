package de.tub.dima.policyliner.dto;

import java.util.List;

public class ViewAttributeDTO {
    private List<String> functionArguments;
    private String functionName;
    private String tableColumnName;
    private String viewColumnName;

    public ViewAttributeDTO(List<String> functionArguments, String functionName, String tableColumnName, String viewColumnName) {
        this.functionArguments = functionArguments;
        this.functionName = functionName;
        this.tableColumnName = tableColumnName;
        this.viewColumnName = viewColumnName;
    }

    public ViewAttributeDTO() {
    }

    public List<String> getFunctionArguments() {
        return functionArguments;
    }

    public void setFunctionArguments(List<String> functionArguments) {
        this.functionArguments = functionArguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getTableColumnName() {
        return tableColumnName;
    }

    public void setTableColumnName(String tableColumnName) {
        this.tableColumnName = tableColumnName;
    }

    public String getViewColumnName() {
        return viewColumnName;
    }

    public void setViewColumnName(String viewColumnName) {
        this.viewColumnName = viewColumnName;
    }
}
