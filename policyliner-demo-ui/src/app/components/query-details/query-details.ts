import {Component, OnInit} from '@angular/core';
import {DatePipe, KeyValuePipe, NgClass} from "@angular/common";
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
    NgClass,
    KeyValuePipe
  ],
  templateUrl: './query-details.html',
  standalone: true,
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
      this.query = new QueryResponseDTO(query);
    })
  }
}
