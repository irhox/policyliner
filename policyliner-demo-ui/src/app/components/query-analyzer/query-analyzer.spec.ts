import {ComponentFixture, TestBed} from '@angular/core/testing';

import {QueryAnalyzer} from './query-analyzer';

describe('QueryAnalyzer', () => {
  let component: QueryAnalyzer;
  let fixture: ComponentFixture<QueryAnalyzer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QueryAnalyzer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QueryAnalyzer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
