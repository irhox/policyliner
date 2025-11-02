import {Component, OnInit} from '@angular/core';
import {DatePipe, KeyValuePipe, NgClass} from "@angular/common";
import {MatButton, MatIconButton} from "@angular/material/button";
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
import {PolicyDTO} from '../../dtos/policy.dto';
import {PolicyService} from '../../services/policy.service';
import {PrivacyMetricDTO} from '../../dtos/privacyMetric.dto';
import {PrivacyMetricService} from '../../services/privacyMetric.service';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-policy-details',
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
    KeyValuePipe,
    MatIconButton,
    MatIcon,
    MatTooltip
  ],
  templateUrl: './policy-details.html',
  standalone: true,
  styleUrl: './policy-details.scss'
})
export class PolicyDetails implements OnInit {
  policyId: string = "";
  policy: PolicyDTO = new PolicyDTO();
  metrics: PrivacyMetricDTO[] = [];

  constructor(
    private route: ActivatedRoute,
    private policyService: PolicyService,
    private privacyMetricService: PrivacyMetricService) {
  }


  ngOnInit(){
    this.route.url.subscribe(u => this.policyId = u[1].path);
    this.policyService.getPolicyById(this.policyId).subscribe(policy => {
      this.policy = new PolicyDTO(policy);
    });
    this.privacyMetricService.getPrivacyMetricsOfPolicy(this.policyId).subscribe(
      metrics => {
        this.metrics = metrics;
      }
    )
  }

  deactivatePolicy() {
    if (this.policyId) this.policyService.deactivatePolicy(this.policyId).subscribe(() =>{window.location.reload()} );
  }

  protected deleteMetric(metricId: string | undefined, events: MouseEvent) {
    events.stopPropagation();
    if (metricId) {
        this.privacyMetricService.deleteMetric(metricId).subscribe(() => {
          window.location.reload();
        });
    }
  }

}
