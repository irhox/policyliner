import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {CreatePolicyDTO} from '../dtos/createPolicy.dto';
import {Observable} from 'rxjs';
import {PolicyDTO} from '../dtos/policy.dto';
import {Injectable} from '@angular/core';
import {SearchDTO} from '../dtos/search.dto';
import {PagedResponseDTO} from '../dtos/pagedResponse.dto';
import {CreatePolicyFromStringDTO} from '../dtos/createPolicyFromString.dto';

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private createPolicyFromObjectUrl: string;
  private createPolicyFromStringUrl: string;
  private searchPoliciesUrl: string;
  private formGetPolicyByIdUrl = (policyId:string)=> environment.baseUrl + `/policy/${policyId}`;

  constructor(private http: HttpClient) {
    this.createPolicyFromObjectUrl = environment.baseUrl + "/policy/create/object";
    this.createPolicyFromStringUrl = environment.baseUrl + "/policy/create/query-string";
    this.searchPoliciesUrl = environment.baseUrl + "/policy/search";
  }

  createPolicyFromObject(createPolicyDTO: CreatePolicyDTO) : Observable<PolicyDTO> {
    return this.http.post(this.createPolicyFromObjectUrl, createPolicyDTO);
  }

  createPolicyFromString(createPolicyFromStringDTO: CreatePolicyFromStringDTO) : Observable<PolicyDTO> {
    return this.http.post(this.createPolicyFromStringUrl, createPolicyFromStringDTO);
  }

  searchPolicies(searchDTO: SearchDTO): Observable<PagedResponseDTO<PolicyDTO>> {
    return this.http.post<PagedResponseDTO<PolicyDTO>>(this.searchPoliciesUrl, searchDTO);
  }

  getPolicyById(policyId: string): Observable<PolicyDTO> {
    return this.http.get<PolicyDTO>(this.formGetPolicyByIdUrl(policyId));
  }
}
