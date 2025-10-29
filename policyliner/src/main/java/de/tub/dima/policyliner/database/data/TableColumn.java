package de.tub.dima.policyliner.database.data;

public class TableColumn {

    private String tableName;
    private String columnName;

    public TableColumn(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return tableName + "." + columnName;
    }
}
