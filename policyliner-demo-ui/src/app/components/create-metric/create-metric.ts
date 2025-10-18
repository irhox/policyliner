import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatButton} from '@angular/material/button';
import {PrivacyMetricDTO} from '../../dtos/privacyMetric.dto';
import {MetricSeverity} from '../../enums/metricSeverity.enum';
import {PrivacyMetricService} from '../../services/privacyMetric.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-create-metric',
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatSelect,
    MatOption,
    MatButton,
  ],
  templateUrl: './create-metric.html',
  styleUrl: './create-metric.scss'
})
export class CreateMetric implements OnInit{
  metricForm: FormGroup;
  severities = Object.values(MetricSeverity);
  policyId: string = "";
  metricId: string | undefined;
  metric: PrivacyMetricDTO = new PrivacyMetricDTO();

  constructor(private router: Router, private route: ActivatedRoute, private fb: FormBuilder, private metricService: PrivacyMetricService) {
    this.route.url.subscribe(u => this.policyId = u[1].path);
    this.metricForm = this.fb.group({
      id: [undefined],
      name: ['', Validators.required],
      description: [''],
      value: ['', Validators.required],
      valueType: ['', Validators.required],
      metricSeverity: ['', Validators.required],
      policyId: [{value: this.policyId, disabled: true}, Validators.required],
    })
  }

  ngOnInit() {
    this.metricId = this.route.snapshot.paramMap.get('metricId') || undefined;

    if (this.metricId) {
      this.metricService.getPrivacyMetricById(this.metricId).subscribe(metric => {
        this.metric = new PrivacyMetricDTO(metric);
        this.metricForm.patchValue(metric);
      });
    }
  }

  onSubmit() {
    if (this.metricForm.invalid) return;
    const body = this.metricForm.getRawValue();
    console.log("BODY: ", body);
    this.metricService.createPrivacyMetric(body).subscribe(() => {
        console.log("BODY: ", body);
        this.metricForm.reset();
        this.router.navigate(['/policy-details', this.policyId]);
    });
  }

  onReset() {
    this.metricForm.reset();
  }

  onCancel() {
    this.router.navigate(['/policy-details', this.policyId]);
  }
}
