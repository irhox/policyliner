import {ChangeDetectorRef, Component, ViewChild} from '@angular/core';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';
import {MatPaginator} from '@angular/material/paginator';
import {MatButton} from '@angular/material/button';
import {Observable} from 'rxjs';
import {AlertDTO} from '../../dtos/alert.dto';
import {MatTableDataSource} from '@angular/material/table';
import {AlertService} from '../../services/alert.service';
import {SearchDTO} from '../../dtos/search.dto';
import {HttpClient} from '@angular/common/http';
import {AsyncPipe, DatePipe} from '@angular/common';

@Component({
  selector: 'app-alerts',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardSubtitle,
    MatCardContent,
    MatCardActions,
    MatPaginator,
    MatButton,
    AsyncPipe,
    DatePipe,
  ],
  templateUrl: './alerts.html',
  styleUrl: './alerts.scss'
})
export class Alerts {

  @ViewChild(MatPaginator) paginator: MatPaginator = new MatPaginator();
  alertsObservable: Observable<AlertDTO[]> = new Observable<AlertDTO[]>();
  dataSource: MatTableDataSource<AlertDTO> = new MatTableDataSource<AlertDTO>();
  private alertService: AlertService;
  filter: string = "";
  sortOrder: string = "desc";
  sortColumn: string = "id";
  pageSize: number = 10;
  pageNumber: number = 0;
  booleanFilter: boolean = false;
  totalElements: number = 0;
  totalPages: number = 0;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private http: HttpClient
    ) {
    this.alertService = new AlertService(http);
  }

  ngOnInit() {
    this.changeDetectorRef.detectChanges();
    this.alertService.searchAlerts(
      new SearchDTO({
        filter: this.filter,
        booleanFilter: this.booleanFilter,
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sortColumn: this.sortColumn,
        sortOrder: this.sortOrder}))
      .subscribe(response => {
          this.dataSource.data = response.elements;
          this.pageNumber = response.currentPage;
          this.pageSize = response.pageSize;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.dataSource.paginator = this.paginator;
          this.alertsObservable = this.dataSource.connect();
    })
  }


}
