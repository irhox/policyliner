import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {QueryRequestDTO} from '../../dtos/queryRequest.dto';
import {QueryService} from '../../services/query.service';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {QueryResponseDTO} from '../../dtos/queryResponse.dto';
import {MatOption, MatSelect} from '@angular/material/select';
import {KeyValuePipe} from '@angular/common';
import {MatChip, MatChipListbox} from '@angular/material/chips';
import {RouterLink} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';

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
    MatButton,
    MatOption,
    MatSelect,
    MatOption,
    MatOption,
    KeyValuePipe,
    MatChip,
    MatChipListbox,
    RouterLink
  ],
  templateUrl: './query-analyzer.html',
  styleUrl: './query-analyzer.scss'
})
export class QueryAnalyzer {

  form: FormGroup;
  loading = false;
  queryResponseDTO: QueryResponseDTO | undefined;
  private _snackBar = inject(MatSnackBar);

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
        this._snackBar.open('Error: ' + err.error.details, 'Close', {
          panelClass: ['error-snackbar'],
        });
        console.error('Error:', err);
        this.loading = false;
      }
    });
  }
}
