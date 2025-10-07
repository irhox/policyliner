import {Component, ViewChild} from '@angular/core';
import {DatePipe, NgClass} from '@angular/common';
import {MatButton, MatFabButton} from '@angular/material/button';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {RouterLink} from '@angular/router';
import {MatIcon} from '@angular/material/icon';
import {MatPaginator} from '@angular/material/paginator';
import {HttpClient} from '@angular/common/http';
import {SearchDTO} from '../../dtos/search.dto';
import {PolicyDTO} from '../../dtos/policy.dto';
import {PolicyService} from '../../services/policy.service';

@Component({
  selector: 'app-policies',
  imports: [
    DatePipe,
    MatButton,
    MatCard,
    MatCardActions,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatFabButton,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    MatPaginator,
    RouterLink,
    NgClass,
    MatCardSubtitle
  ],
  templateUrl: './policies.html',
  styleUrl: './policies.scss'
})
export class Policies {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  policies: PolicyDTO[] = [];
  private policyService: PolicyService;
  filter: string = "";
  pageSize: number = 10;
  pageNumber: number = 0;
  booleanFilter: boolean = false;
  totalElements: number = 0;
  totalPages: number = 0;

  constructor(
    private http: HttpClient
  ) {
    this.policyService = new PolicyService(http);
  }

  ngOnInit() {
    this.loadPolicies();
  }

  ngAfterViewInit() {
    this.paginator.page.subscribe(() => {
      this.pageNumber = this.paginator.pageIndex;
      this.pageSize = this.paginator.pageSize;
      this.loadPolicies();
    });
  }

  filterPolicies(filter: string) {
    this.filter = filter;
    this.pageNumber = 0;
    this.loadPolicies();
  }


  private loadPolicies() {
    let searchDTO = new SearchDTO({
      filter: this.filter,
      booleanFilter: this.booleanFilter,
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      sortColumn: "",
      sortOrder: ""
    });

    this.policyService.searchPolicies(
      searchDTO)
      .subscribe(response => {
        this.policies = response.elements;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
      })
  }
}
