import {QueryComparatorType} from './queryComparatorType.enum';

export class QueryRequestDTO {
  query?: string;
  userId?: string;
  userRole?: string;
  comparatorType?: QueryComparatorType;

  constructor(obj?: any) {
    if (obj) {
      this.query = obj.query;
      this.userId = obj.userId;
      this.userRole = obj.userRole;
      this.comparatorType = obj.comparatorType;
    }
  }

}
