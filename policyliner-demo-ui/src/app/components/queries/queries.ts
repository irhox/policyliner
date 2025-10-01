import {Component, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {HttpClient} from '@angular/common/http';
import {SearchDTO} from '../../dtos/search.dto';
import {QueryResponseDTO} from '../../dtos/queryResponse.dto';
import {QueryService} from '../../services/query.service';
import {DatePipe, NgClass} from '@angular/common';
import {MatButton, MatFabButton} from '@angular/material/button';
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {RouterLink} from '@angular/router';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-queries',
  imports: [
    MatButton,
    MatCard,
    MatCardActions,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatPaginator,
    DatePipe,
    MatFabButton,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    RouterLink,
    NgClass
  ],
  templateUrl: './queries.html',
  standalone: true,
  styleUrl: './queries.scss'
})
export class Queries {

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  queries: QueryResponseDTO[] = [];
  private queryService: QueryService;
  filter: string = "";
  pageSize: number = 10;
  pageNumber: number = 0;
  booleanFilter: boolean = false;
  totalElements: number = 0;
  totalPages: number = 0;

  constructor(
    private http: HttpClient
  ) {
    this.queryService = new QueryService(http);
  }

  ngOnInit() {
    this.loadQueries();
  }

  ngAfterViewInit() {
    this.paginator.page.subscribe(() => {
      this.pageNumber = this.paginator.pageIndex;
      this.pageSize = this.paginator.pageSize;
      this.loadQueries();
    });
  }

  filterQueries(filter: string) {
    this.filter = filter;
    this.pageNumber = 0;
    this.loadQueries();
  }


  private loadQueries() {
    let searchDTO = new SearchDTO({
      filter: this.filter,
      booleanFilter: this.booleanFilter,
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      sortColumn: "",
      sortOrder: ""
    });

    this.queryService.searchQueries(
      searchDTO)
      .subscribe(response => {
        this.queries = response.elements;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
      })
  }

}
