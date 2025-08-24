package de.tub.dima.policyliner.services;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class QueryAdapter {

    public String removeWhereClauseFromQuery(String query) {
        return query.substring(0, query.lastIndexOf("WHERE"));
    }

    public String runUserDefinedFunctionOnTableColumn(String query, String tableColumnName, String functionName, List<String> functionArguments){
        StringBuilder udfColumn = new StringBuilder(functionName + "(" + tableColumnName);
        if (functionArguments != null && !functionArguments.isEmpty()) {
            for (String functionArgument : functionArguments) {
                udfColumn.append(", ").append(functionArgument);
            }
        }
        udfColumn.append(")");
        return query.replaceAll(tableColumnName, udfColumn.toString());
    }
    
}
