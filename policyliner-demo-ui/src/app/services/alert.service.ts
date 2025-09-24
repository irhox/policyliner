import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {AlertDTO} from '../dtos/alert.dto';
import {PagedResponseDTO} from '../dtos/pagedResponse.dto';
import {SearchDTO} from '../dtos/search.dto';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private searchAlertsUrl: string;

  constructor(private http: HttpClient) {
    this.searchAlertsUrl = environment.baseUrl + "/alert/search";
  }

  searchAlerts(searchDTO: SearchDTO): Observable<PagedResponseDTO<AlertDTO>> {
    return this.http.post<PagedResponseDTO<AlertDTO>>(this.searchAlertsUrl, searchDTO);
  }

}
