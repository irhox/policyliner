export class TableInfoDTO {
  tableName?: string;
  primaryKey?: string;
  foreignKey?: string;

  constructor(obj?: any) {
    if (obj) {
      this.tableName = obj.tableName;
      this.primaryKey = obj.primaryKey;
      this.foreignKey = obj.foreignKey;
    }
  }
}
