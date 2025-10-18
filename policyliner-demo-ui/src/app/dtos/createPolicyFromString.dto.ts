export class CreatePolicyFromStringDTO {
  policy?: string;
  isMaterializedView?: boolean;
  useDefaultMetrics?: boolean;

  constructor(obj?: any) {
    if (obj) {
      this.policy = obj.policy;
      this.isMaterializedView = obj.isMaterializedView;
      this.useDefaultMetrics = obj.useDefaultMetrics;
    }
  }
}
