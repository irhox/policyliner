import {ViewAttributeDTO} from './viewAttribute.dto';

export class CreatePolicyDTO {
  policyName?: string;
  tableName?: string;
  columns?: ViewAttributeDTO[];

  public CreatePolicyDTO(obj?: any) {
    if (obj) {
      this.policyName = obj.policyName;
      this.tableName = obj.tableName;
      this.columns = obj.columns;
    }
  }
}
