import {QuasiIdentifierDTO} from './quasiIdentifier.dto';

export class CreatePolicyFromStringDTO {
  policy?: string;
  isMaterializedView?: boolean;
  useDefaultMetrics?: boolean;
  evaluatePolicyUponCreation?: boolean;
  quasiIdentifier?: QuasiIdentifierDTO;

  constructor(obj?: any) {
    if (obj) {
      this.policy = obj.policy;
      this.isMaterializedView = obj.isMaterializedView;
      this.useDefaultMetrics = obj.useDefaultMetrics;
      this.evaluatePolicyUponCreation = obj.evaluatePolicyUponCreation;
      this.quasiIdentifier = obj.quasiIdentifier;
    }
  }
}
