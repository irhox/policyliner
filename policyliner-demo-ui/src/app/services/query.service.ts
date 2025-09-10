import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {QueryRequestDTO} from '../dtos/queryRequest.dto';
import {Observable} from 'rxjs';
import {QueryResponseDTO} from '../dtos/queryResponse.dto';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class QueryService {
  private analyzeQueryUrl: string;

  constructor(private http: HttpClient) {
    this.analyzeQueryUrl = environment.baseUrl + "/query/analyze";
  }

  analyzeQuery(query: QueryRequestDTO): Observable<QueryResponseDTO> {
    return this.http.post(this.analyzeQueryUrl, query);
  }

}
