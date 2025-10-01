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

  private formResolveAlertUrl = (alertId:string)=> environment.baseUrl + `/alert/${alertId}/resolve`;
  private formGetAlertByUrl = (alertId:string)=> environment.baseUrl + `/alert/${alertId}`;

  searchAlerts(searchDTO: SearchDTO): Observable<PagedResponseDTO<AlertDTO>> {
    return this.http.post<PagedResponseDTO<AlertDTO>>(this.searchAlertsUrl, searchDTO);
  }

  resolveAlert(alertId: string): Observable<AlertDTO> {
    return this.http.put<AlertDTO>(this.formResolveAlertUrl(alertId), null);
  }

  getAlertById(alertId: string): Observable<AlertDTO> {
    return this.http.get<AlertDTO>(this.formGetAlertByUrl(alertId));
  }
}
