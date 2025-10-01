import {Component, OnInit} from '@angular/core';
import {DatePipe, NgClass} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from "@angular/material/card";
import {MatChip, MatChipListbox} from "@angular/material/chips";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {QueryResponseDTO} from '../../dtos/queryResponse.dto';
import {QueryService} from '../../services/query.service';

@Component({
  selector: 'app-query-details',
  imports: [
    DatePipe,
    MatButton,
    MatCard,
    MatCardActions,
    MatCardContent,
    MatCardHeader,
    MatCardSubtitle,
    MatCardTitle,
    MatChip,
    MatChipListbox,
    RouterLink,
    NgClass
  ],
  templateUrl: './query-details.html',
  styleUrl: './query-details.scss'
})
export class QueryDetails implements OnInit {
  queryId: string = "";
  query: QueryResponseDTO = new QueryResponseDTO();

  constructor(private route: ActivatedRoute, private queryService: QueryService) {
  }


  ngOnInit(){
    this.route.url.subscribe(u => this.queryId = u[1].path);
    this.queryService.getQueryById(this.queryId).subscribe(query => {
      this.query = query;
      console.log(this.query);
    })
  }
}
