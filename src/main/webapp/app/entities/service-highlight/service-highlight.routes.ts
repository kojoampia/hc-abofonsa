import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ServiceHighlightResolve from './route/service-highlight-routing-resolve.service';

const serviceHighlightRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/service-highlight').then(m => m.ServiceHighlight),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/service-highlight-detail').then(m => m.ServiceHighlightDetail),
    resolve: {
      serviceHighlight: ServiceHighlightResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/service-highlight-update').then(m => m.ServiceHighlightUpdate),
    resolve: {
      serviceHighlight: ServiceHighlightResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/service-highlight-update').then(m => m.ServiceHighlightUpdate),
    resolve: {
      serviceHighlight: ServiceHighlightResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default serviceHighlightRoute;
