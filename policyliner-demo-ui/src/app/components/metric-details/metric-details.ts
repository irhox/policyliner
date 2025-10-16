import {Component, OnInit} from '@angular/core';
import {PrivacyMetricDTO} from '../../dtos/privacyMetric.dto';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {PrivacyMetricService} from '../../services/privacyMetric.service';
import {MatButton} from '@angular/material/button';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';

@Component({
  selector: 'app-metric-details',
  imports: [
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardSubtitle,
    MatCardTitle,
    RouterLink,
    MatCardActions,
    MatButton
  ],
  templateUrl: './metric-details.html',
  standalone: true,
  styleUrl: './metric-details.scss'
})
export class MetricDetails implements OnInit {
  metricId: string = "";
  metric: PrivacyMetricDTO = new PrivacyMetricDTO();

  constructor(
    private route: ActivatedRoute,
    private metricService: PrivacyMetricService) {
  }

  ngOnInit() {
    this.route.url.subscribe(u => this.metricId = u[1].path);
    this.metricService.getPrivacyMetricById(this.metricId).subscribe(metric => {
      this.metric = new PrivacyMetricDTO(metric);
    });
  }

}
