import {PolicyStatus} from '../enums/policyStatus.enum';

export class PolicyDTO {
  id?: string;
  policy?: string;
  status?: PolicyStatus;
  deactivatedAt?: Date;

  constructor(obj?: any) {
    if (obj) {
      this.id = obj.id;
      this.policy = obj.policy;
      this.status = obj.status;
      this.deactivatedAt = obj.deactivatedAt;
    }
  }
}
