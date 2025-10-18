import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CreateMetric} from './create-metric';

describe('CreateMetric', () => {
  let component: CreateMetric;
  let fixture: ComponentFixture<CreateMetric>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateMetric]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateMetric);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
