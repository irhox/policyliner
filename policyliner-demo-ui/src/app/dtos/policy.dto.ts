import {PolicyStatus} from '../enums/policyStatus.enum';
import {AlertSeverity} from '../enums/alertSeverity.enum';

export class PolicyDTO {
  id?: string;
  policy?: string;
  status?: PolicyStatus;
  createdAt?: Date;
  allowedUserRole?: string;
  viewName?: string;
  materializedViewName?: string;
  deactivatedAt?: Date;
  alertSeverityMap?: Map<string, AlertSeverity>;

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
      this.alertSeverityMap = new Map(Object.entries(obj.alertSeverityMap));
    }
  }
}
