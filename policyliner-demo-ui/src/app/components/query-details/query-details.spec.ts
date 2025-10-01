import {ComponentFixture, TestBed} from '@angular/core/testing';

import {QueryDetails} from './query-details';

describe('QueryDetails', () => {
  let component: QueryDetails;
  let fixture: ComponentFixture<QueryDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QueryDetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QueryDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
