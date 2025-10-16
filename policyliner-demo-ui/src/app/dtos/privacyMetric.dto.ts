import {MetricSeverity} from '../enums/metricSeverity.enum';

export class PrivacyMetricDTO {
  id?: string;
  name?: string;
  description?: string;
  value?: string;
  valueType?: string;
  metricSeverity?: MetricSeverity;
  policyId?: string;

  constructor(obj?: any) {
    if (obj) {
      this.id = obj.id;
      this.name = obj.name;
      this.description = obj.description;
      this.value = obj.value;
      this.valueType = obj.valueType;
      this.metricSeverity = obj.metricSeverity;
      this.policyId = obj.policyId;
    }
  }
}
