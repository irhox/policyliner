import {QueryStatus} from './queryStatus.enum';

export class QueryResponseDTO {
  id?: string;
  query?: string;
  status?: QueryStatus;
  message?: string;

  constructor(obj?: any) {
    if (obj) {
      this.id = obj.id;
      this.query = obj.query;
      this.status = obj.status;
      this.message = obj.message;
    }
  }
}
