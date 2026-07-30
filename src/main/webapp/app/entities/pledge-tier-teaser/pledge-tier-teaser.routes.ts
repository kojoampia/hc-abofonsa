import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PledgeTierTeaserResolve from './route/pledge-tier-teaser-routing-resolve.service';

const pledgeTierTeaserRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/pledge-tier-teaser').then(m => m.PledgeTierTeaser),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/pledge-tier-teaser-detail').then(m => m.PledgeTierTeaserDetail),
    resolve: {
      pledgeTierTeaser: PledgeTierTeaserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/pledge-tier-teaser-update').then(m => m.PledgeTierTeaserUpdate),
    resolve: {
      pledgeTierTeaser: PledgeTierTeaserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/pledge-tier-teaser-update').then(m => m.PledgeTierTeaserUpdate),
    resolve: {
      pledgeTierTeaser: PledgeTierTeaserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default pledgeTierTeaserRoute;
