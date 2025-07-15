import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {CreatePolicyDTO} from '../dtos/createPolicy.dto';
import {Observable} from 'rxjs';
import {PolicyDTO} from '../dtos/policy.dto';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private createPolicyFromObjectUrl: string;
  private createPolicyFromStringUrl: string;

  constructor(private http: HttpClient) {
    this.createPolicyFromObjectUrl = environment.baseUrl + "/policy/create/object";
    this.createPolicyFromStringUrl = environment.baseUrl + "/policy/create/query-string";
  }

  createPolicyFromObject(createPolicyDTO: CreatePolicyDTO) : Observable<PolicyDTO> {
    return this.http.post(this.createPolicyFromObjectUrl, createPolicyDTO);
  }

  createPolicyFromString(policyStatement: string) : Observable<PolicyDTO> {
    return this.http.post(this.createPolicyFromStringUrl, policyStatement);
  }
}
