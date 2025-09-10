import {TableInfoDTO} from './tableInfo.dto';
import {ViewAttributeDTO} from './viewAttribute.dto';

export class CreatePolicyDTO {
  policyName?: string;
  tables?: TableInfoDTO[];
  columns?: ViewAttributeDTO[];

  constructor(obj?: any) {
    if (obj) {
      this.policyName = obj.policyName;
      this.tables = obj.tables;
      this.columns = obj.columns;
    }
  }
}
