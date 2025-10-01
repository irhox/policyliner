import {ComponentFixture, TestBed} from '@angular/core/testing';

import {Queries} from './queries';

describe('Queries', () => {
  let component: Queries;
  let fixture: ComponentFixture<Queries>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Queries]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Queries);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
