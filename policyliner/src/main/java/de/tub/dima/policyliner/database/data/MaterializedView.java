package de.tub.dima.policyliner.database.data;

public class MaterializedView {
    private String viewName;
    private Boolean isPopulated;
    private String definition;

    public MaterializedView(String viewName, Boolean isPopulated, String definition) {
        this.viewName = viewName;
        this.isPopulated = isPopulated;
        this.definition = definition;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Boolean getPopulated() {
        return isPopulated;
    }

    public void setPopulated(Boolean populated) {
        isPopulated = populated;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "MaterializedView{" +
                "viewName='" + viewName + '\'' +
                ", isPopulated=" + isPopulated +
                ", definition='" + definition + '\'' +
                '}';
    }
}
