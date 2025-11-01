package de.tub.dima.policyliner.entities;

import java.util.List;

public class JsonQuasiIdentifier {
    private String viewName;
    private List<String> columns;
    private List<String> sensitiveAttributes;

    public JsonQuasiIdentifier(String viewName, List<String> columns, List<String> sensitiveAttributes) {
        this.viewName = viewName;
        this.columns = columns;
        this.sensitiveAttributes = sensitiveAttributes;
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
    public List<String> getSensitiveAttributes() {
        return sensitiveAttributes;
    }
    public void setSensitiveAttributes(List<String> sensitiveAttributes) {
        this.sensitiveAttributes = sensitiveAttributes;
    }
}
