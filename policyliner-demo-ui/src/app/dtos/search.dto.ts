export class SearchDTO {
  filter?: string;
  sortOrder?: string;
  sortColumn?: string;
  pageSize?: number;
  pageNumber?: number;
  booleanFilter?: boolean;

  constructor(obj?: any) {
    if (obj) {
      this.filter = obj.filter;
      this.sortOrder = obj.sortOrder;
      this.sortColumn = obj.sortColumn;
      this.pageSize = obj.pageSize;
      this.pageNumber = obj.pageNumber;
      this.booleanFilter = obj.booleanFilter;
    }
  }
}
