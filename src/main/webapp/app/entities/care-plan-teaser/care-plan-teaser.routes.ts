import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CarePlanTeaserResolve from './route/care-plan-teaser-routing-resolve.service';

const carePlanTeaserRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/care-plan-teaser').then(m => m.CarePlanTeaser),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/care-plan-teaser-detail').then(m => m.CarePlanTeaserDetail),
    resolve: {
      carePlanTeaser: CarePlanTeaserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/care-plan-teaser-update').then(m => m.CarePlanTeaserUpdate),
    resolve: {
      carePlanTeaser: CarePlanTeaserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/care-plan-teaser-update').then(m => m.CarePlanTeaserUpdate),
    resolve: {
      carePlanTeaser: CarePlanTeaserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default carePlanTeaserRoute;
