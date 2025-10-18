import {Component} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {PolicyService} from '../../services/policy.service';
import {PolicyDTO} from '../../dtos/policy.dto';
import {MatCheckbox} from '@angular/material/checkbox';
import {Router} from '@angular/router';

@Component({
  selector: 'app-policy-creation',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatInput,
    MatLabel,
    MatFormField,
    MatButton,
    MatCheckbox,
  ],
  templateUrl: './policy-creation.html',
  styleUrl: './policy-creation.scss'
})
export class PolicyCreation {

  form: FormGroup;
  loading = false;
  policyDTO: PolicyDTO = new PolicyDTO();

  constructor(private fb: FormBuilder, private policyService: PolicyService, private router: Router) {
    this.form = this.fb.group({
      policy: ['', Validators.required],
      isMaterializedView: [false],
      useDefaultMetrics: [false],
    });
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.loading = true;
    const body = this.form.getRawValue();
    this.policyService.createPolicyFromString(body).subscribe({
      next: (response) => {
        console.log('Success:', response);
        this.policyDTO = new PolicyDTO(response);
        this.loading = false;
        this.router.navigate(['/policy-details', this.policyDTO.id]);
      },
      error: (err) => {
        console.error('Error:', err);
        this.loading = false;
      }
    });
  }
}
