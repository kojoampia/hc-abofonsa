import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MetricRollupResolve from './route/metric-rollup-routing-resolve.service';

const metricRollupRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/metric-rollup').then(m => m.MetricRollup),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/metric-rollup-detail').then(m => m.MetricRollupDetail),
    resolve: {
      metricRollup: MetricRollupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default metricRollupRoute;
