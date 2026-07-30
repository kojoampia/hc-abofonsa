import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CareServiceTeaserResolve from './route/care-service-teaser-routing-resolve.service';

const careServiceTeaserRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/care-service-teaser').then(m => m.CareServiceTeaser),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/care-service-teaser-detail').then(m => m.CareServiceTeaserDetail),
    resolve: {
      careServiceTeaser: CareServiceTeaserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/care-service-teaser-update').then(m => m.CareServiceTeaserUpdate),
    resolve: {
      careServiceTeaser: CareServiceTeaserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/care-service-teaser-update').then(m => m.CareServiceTeaserUpdate),
    resolve: {
      careServiceTeaser: CareServiceTeaserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default careServiceTeaserRoute;
