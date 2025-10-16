import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MetricDetails} from './metric-details';

describe('MetricDetails', () => {
  let component: MetricDetails;
  let fixture: ComponentFixture<MetricDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetricDetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MetricDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
