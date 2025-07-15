import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {PolicyService} from '../../services/policy.service';
import {PolicyDTO} from '../../dtos/policy.dto';

@Component({
  selector: 'app-policy-creation',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatInput,
    MatLabel,
    MatFormField,
    MatButton,
  ],
  templateUrl: './policy-creation.html',
  styleUrl: './policy-creation.scss'
})
export class PolicyCreation {

  form: FormGroup;
  loading = false;
  policyDTO: PolicyDTO = new PolicyDTO();

  constructor(private fb: FormBuilder, private policyService: PolicyService) {
    this.form = this.fb.group({
      inputText: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.loading = true;
    const body = this.form.value.inputText;

    this.policyService.createPolicyFromString(body).subscribe({
      next: (response) => {
        console.log('Success:', response);
        this.policyDTO = new PolicyDTO(response);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error:', err);
        this.loading = false;
      }
    });
  }
}
