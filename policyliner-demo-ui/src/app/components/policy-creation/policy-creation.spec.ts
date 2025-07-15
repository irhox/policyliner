import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PolicyCreation } from './policy-creation';

describe('PolicyCreation', () => {
  let component: PolicyCreation;
  let fixture: ComponentFixture<PolicyCreation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PolicyCreation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PolicyCreation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
