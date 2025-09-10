import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {QueryRequestDTO} from '../../dtos/queryRequest.dto';
import {QueryService} from '../../services/query.service';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {QueryResponseDTO} from '../../dtos/queryResponse.dto';

@Component({
  selector: 'app-query-analyzer',
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatFormField,
    MatInput,
    MatFormField,
    MatLabel,
    MatButton
  ],
  templateUrl: './query-analyzer.html',
  styleUrl: './query-analyzer.scss'
})
export class QueryAnalyzer {

  form: FormGroup;
  loading = false;
  queryResponseDTO: QueryResponseDTO | undefined;

  constructor(private fb: FormBuilder, private queryService: QueryService) {
    this.form = this.fb.group({
      query: ['', Validators.required],
      userId: ['', Validators.required],
      userRole: [''],
      comparatorType: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.loading = true;
    const body = this.form.value;
    console.log("BODY: ", body);
    this.queryService.analyzeQuery(new QueryRequestDTO(body)).subscribe({
      next: (response) => {
        console.log('Success:', response);
        this.queryResponseDTO = new QueryResponseDTO(response);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error:', err);
        this.loading = false;
      }
    });
  }
}
