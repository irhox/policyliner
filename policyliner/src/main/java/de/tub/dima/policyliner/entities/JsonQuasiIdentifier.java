package de.tub.dima.policyliner.entities;

import java.util.List;

public class JsonQuasiIdentifier {
    private String viewName;
    private List<String> columns;

    public JsonQuasiIdentifier(String viewName, List<String> columns) {
        this.viewName = viewName;
        this.columns = columns;
    }

    public String getViewName() {
        return viewName;
    }
    public List<String> getColumns() {
        return columns;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
