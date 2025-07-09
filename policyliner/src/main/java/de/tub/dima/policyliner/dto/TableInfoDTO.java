package de.tub.dima.policyliner.dto;

public class TableInfoDTO {
    private String tableName;
    private String primaryKey;
    private String foreignKey;

    public TableInfoDTO(String tableName, String primaryKey, String foreignKey) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.foreignKey = foreignKey;
    }

    public TableInfoDTO() {
    }

    public String getTableName() {
        return tableName;
    }
    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }
}
