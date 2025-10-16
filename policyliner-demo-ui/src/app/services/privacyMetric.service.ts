import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {PrivacyMetricDTO} from '../dtos/privacyMetric.dto';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PrivacyMetricService {
  private createPrivacyMetricUrl: string;
  private formGetPrivacyMetricsOfPolicyUrl = (policyId:string)=> environment.baseUrl + `/privacy-metric/of-policy/${policyId}`;
  private formGetPrivacyMetricUrl = (privacyMetricId:string)=> environment.baseUrl + `/privacy-metric/${privacyMetricId}`;

  constructor(private http: HttpClient) {
    this.createPrivacyMetricUrl = environment.baseUrl + "/privacy-metric/create";
  }

  createPrivacyMetric(privacyMetric: PrivacyMetricDTO): Observable<PrivacyMetricDTO> {
    return this.http.put<PrivacyMetricDTO>(this.createPrivacyMetricUrl, privacyMetric);
  }

  getPrivacyMetricsOfPolicy(policyId: string): Observable<PrivacyMetricDTO[]> {
    return this.http.get<PrivacyMetricDTO[]>(this.formGetPrivacyMetricsOfPolicyUrl(policyId));
  }

  getPrivacyMetricById(privacyMetricId: string): Observable<PrivacyMetricDTO> {
    return this.http.get<PrivacyMetricDTO>(this.formGetPrivacyMetricUrl(privacyMetricId));
  }
}
