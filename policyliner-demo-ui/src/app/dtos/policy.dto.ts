import {PolicyStatus} from '../enums/policyStatus.enum';

export class PolicyDTO {
  id?: string;
  policy?: string;
  status?: PolicyStatus;
  createdAt?: Date;
  allowedUserRole?: string;
  viewName?: string;
  materializedViewName?: string;
  deactivatedAt?: Date;
  alertIdList?: string[];

  constructor(obj?: any) {
    if (obj) {
      this.id = obj.id;
      this.policy = obj.policy;
      this.status = obj.status;
      this.deactivatedAt = obj.deactivatedAt;
      this.createdAt = obj.createdAt;
      this.allowedUserRole = obj.allowedUserRole;
      this.viewName = obj.viewName;
      this.materializedViewName = obj.materializedViewName;
      this.alertIdList = obj.alertIdList;
    }
  }
}
