import {Routes} from '@angular/router';
import {HomeComponent} from './components/home-component/home-component';
import {PolicyCreation} from './components/policy-creation/policy-creation';
import {QueryAnalyzer} from './components/query-analyzer/query-analyzer';
import {Alerts} from './components/alerts/alerts';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'policy-creation',
    component: PolicyCreation,
  },
  {
    path: 'query-analyzer',
    component: QueryAnalyzer,
  },
  {
    path: 'alerts',
    component: Alerts,
  }
];
