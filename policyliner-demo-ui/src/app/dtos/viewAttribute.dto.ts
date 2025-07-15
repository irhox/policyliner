export class ViewAttributeDTO {
  functionArguments?: string[];
  functionName?: string;
  tableColumnName?: string;
  viewColumnName?: string;

  public ViewAttributeDTO(obj?: any) {
    if (obj) {
      this.functionArguments = obj.functionArguments;
      this.functionName = obj.functionName;
      this.tableColumnName = obj.tableColumnName;
      this.viewColumnName = obj.viewColumnName;
    }
  }
}
