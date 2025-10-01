import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {QueryRequestDTO} from '../dtos/queryRequest.dto';
import {Observable} from 'rxjs';
import {QueryResponseDTO} from '../dtos/queryResponse.dto';
import {Injectable} from '@angular/core';
import {SearchDTO} from '../dtos/search.dto';
import {PagedResponseDTO} from '../dtos/pagedResponse.dto';

@Injectable({
  providedIn: 'root'
})
export class QueryService {
  private analyzeQueryUrl: string;
  private searchQueryUrl: string;
  private formGetQueryByIdUrl = (queryId:string)=> environment.baseUrl + `/query/${queryId}`;

  constructor(private http: HttpClient) {
    this.analyzeQueryUrl = environment.baseUrl + "/query/analyze";
    this.searchQueryUrl = environment.baseUrl + "/query/search";
  }



  analyzeQuery(query: QueryRequestDTO): Observable<QueryResponseDTO> {
    return this.http.post(this.analyzeQueryUrl, query);
  }

  searchQueries(searchDTO: SearchDTO): Observable<PagedResponseDTO<QueryResponseDTO>> {
    return this.http.post<PagedResponseDTO<QueryResponseDTO>>(this.searchQueryUrl, searchDTO);
  }

  getQueryById(queryId: string): Observable<QueryResponseDTO> {
    return this.http.get<QueryResponseDTO>(this.formGetQueryByIdUrl(queryId));
  }

}
