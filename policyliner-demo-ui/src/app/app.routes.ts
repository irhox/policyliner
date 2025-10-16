import {Routes} from '@angular/router';
import {HomeComponent} from './components/home-component/home-component';
import {PolicyCreation} from './components/policy-creation/policy-creation';
import {QueryAnalyzer} from './components/query-analyzer/query-analyzer';
import {Alerts} from './components/alerts/alerts';
import {Queries} from './components/queries/queries';
import {AlertDetails} from './components/alert-details/alert-details';
import {QueryDetails} from './components/query-details/query-details';
import {Policies} from './components/policies/policies';
import {PolicyDetails} from './components/policy-details/policy-details';
import {MetricDetails} from './components/metric-details/metric-details';

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
  },
  {
    path: 'alert-details/:id',
    component: AlertDetails,
  },
  {
    path: 'queries',
    component: Queries,
  },
  {
    path: 'query-details/:id',
    component: QueryDetails,
  },
  {
    path: 'policies',
    component: Policies,
  },
  {
    path: 'policy-details/:id',
    component: PolicyDetails,
  },
  {
    path: 'metric-details/:id',
    component: MetricDetails,
  }
];
