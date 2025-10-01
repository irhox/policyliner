import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {AlertService} from '../../services/alert.service';
import {AlertDTO} from '../../dtos/alert.dto';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';
import {MatChip, MatChipListbox} from '@angular/material/chips';
import {MatButton} from '@angular/material/button';
import {DatePipe, NgClass} from '@angular/common';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-alert-details',
  imports: [
    MatCardHeader,
    MatCardTitle,
    MatCardSubtitle,
    MatCard,
    MatCardContent,
    MatChip,
    MatChipListbox,
    MatCardActions,
    MatButton,
    RouterLink,
    DatePipe,
    MatIcon,
    NgClass,
  ],
  templateUrl: './alert-details.html',
  standalone: true,
  styleUrl: './alert-details.scss'
})
export class AlertDetails implements OnInit {
  alertId: string = "";
  alert: AlertDTO = new AlertDTO();

  constructor(private route: ActivatedRoute, private alertService: AlertService) {
  }


  ngOnInit(){
    this.route.url.subscribe(u => this.alertId = u[1].path);
    this.alertService.getAlertById(this.alertId).subscribe(alert => {
      this.alert = alert;
    })
  }

  resolveAlert() {
    if (this.alertId) this.alertService.resolveAlert(this.alertId).subscribe(() =>{window.location.reload()} );
  }

}
