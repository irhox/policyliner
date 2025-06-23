package de.tub.dima.policyliner.database.data;

public class TableInformation {

    private String tableName;
    private String tableSchema;

    public TableInformation(String tableName, String tableSchema) {
        this.tableName = tableName;
        this.tableSchema = tableSchema;
    }

    public TableInformation() {
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    @Override
    public String toString() {
        return "TableInformation{" +
                "tableName='" + tableName + '\'' +
                ", tableSchema='" + tableSchema + '\'' +
                '}';
    }
}
