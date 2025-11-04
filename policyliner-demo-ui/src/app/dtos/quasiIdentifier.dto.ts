export class QuasiIdentifierDTO {
  viewName?: string;
  columns?: string[];
  sensitiveAttributes?: string[];

  constructor(obj?: any) {
    if (obj) {
      this.viewName = obj.viewName;
      this.columns = obj.columns;
      this.sensitiveAttributes = obj.sensitiveAttributes;
    }
  }
}
