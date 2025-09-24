import {AlertType} from '../enums/alertType.enum';
import {AlertSeverity} from '../enums/alertSeverity.enum';

export class AlertDTO {
  id?: string;
  type?: AlertType;
  severity?: AlertSeverity;
  message?: string;
  isResolved?: boolean;
  createdAt?: Date;

  public constructor(obj?: any) {
    if (obj) {
      this.id = obj.id;
      this.type = obj.type;
      this.severity = obj.severity;
      this.message = obj.message;
      this.isResolved = obj.isResolved;
      this.createdAt = obj.createdAt;
    }
  }

}
