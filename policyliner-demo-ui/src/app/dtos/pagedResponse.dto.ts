export class PagedResponseDTO<T> {
  elements: T[] = [];
  totalElements: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;
  pageSize: number = 0;

  constructor(obj: any) {
    if (obj){
      this.elements = obj.elements;
      this.totalElements = obj.totalElements;
      this.totalPages = obj.totalPages;
      this.currentPage = obj.currentPage;
      this.pageSize = obj.pageSize;
    }

  }
}
