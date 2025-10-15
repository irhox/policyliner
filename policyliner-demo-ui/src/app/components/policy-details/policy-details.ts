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
import {PolicyDTO} from '../../dtos/policy.dto';
import {PolicyService} from '../../services/policy.service';

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
    KeyValuePipe
  ],
  templateUrl: './policy-details.html',
  standalone: true,
  styleUrl: './policy-details.scss'
})
export class PolicyDetails implements OnInit {
  policyId: string = "";
  policy: PolicyDTO = new PolicyDTO();

  constructor(private route: ActivatedRoute, private policyService: PolicyService) {
  }


  ngOnInit(){
    this.route.url.subscribe(u => this.policyId = u[1].path);
    this.policyService.getPolicyById(this.policyId).subscribe(policy => {
      this.policy = new PolicyDTO(policy);
    })
  }
}
