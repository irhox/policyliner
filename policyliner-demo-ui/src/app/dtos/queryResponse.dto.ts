import {QueryStatus} from '../enums/queryStatus.enum';
import {QueryInspectionStatus} from '../enums/queryInspectionStatus.enum';

export class QueryResponseDTO {
  id?: string;
  userId?: string;
  query?: string;
  status?: QueryStatus;
  message?: string;
  createdAt?: Date;
  inspectionStatus?: QueryInspectionStatus;
  alertIdList?: string[];

  constructor(obj?: any) {
    if (obj) {
      this.id = obj.id;
      this.query = obj.query;
      this.status = obj.status;
      this.message = obj.message;
      this.createdAt = obj.createdAt;
      this.inspectionStatus = obj.inspectionStatus;
      this.alertIdList = obj.alertIdList;
    }
  }
}
