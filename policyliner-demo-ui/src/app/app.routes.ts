import { Routes } from '@angular/router';
import {HomeComponent} from './components/home-component/home-component';
import {PolicyCreation} from './components/policy-creation/policy-creation';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'policy-creation',
    component: PolicyCreation,
  },
];
