import {AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';
import {MatPaginator} from '@angular/material/paginator';
import {MatButton, MatFabButton} from '@angular/material/button';
import {AlertDTO} from '../../dtos/alert.dto';
import {AlertService} from '../../services/alert.service';
import {SearchDTO} from '../../dtos/search.dto';
import {HttpClient} from '@angular/common/http';
import {DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';

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
    DatePipe,
    RouterLink,
    MatInput,
    MatLabel,
    MatFormField,
    MatIcon,
    MatFabButton,
  ],
  templateUrl: './alerts.html',
  standalone: true,
  styleUrl: './alerts.scss'
})
export class Alerts implements OnInit, AfterViewInit {

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  alerts: AlertDTO[] = [];
  private alertService: AlertService;
  filter: string = "";
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
    this.loadAlerts();
  }

  ngAfterViewInit() {
    this.paginator.page.subscribe(() => {
      this.pageNumber = this.paginator.pageIndex;
      this.pageSize = this.paginator.pageSize;
      this.loadAlerts();
    });
  }

  resolveAlert(alertId: string | undefined) {
    this.changeDetectorRef.detectChanges();
    if (alertId) this.alertService.resolveAlert(alertId).subscribe(() =>{window.location.reload()} );
  }

  filterAlerts(filter: string) {
    this.filter = filter;
    this.pageNumber = 0;
    this.loadAlerts();
  }


  private loadAlerts() {
    let searchDTO = new SearchDTO({
      filter: this.filter,
      booleanFilter: this.booleanFilter,
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      sortColumn: "",
      sortOrder: ""
    });

    this.alertService.searchAlerts(
      searchDTO)
      .subscribe(response => {
        this.alerts = response.elements;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
      })
  }
}
