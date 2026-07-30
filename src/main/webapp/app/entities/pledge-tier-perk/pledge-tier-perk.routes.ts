import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PledgeTierPerkResolve from './route/pledge-tier-perk-routing-resolve.service';

const pledgeTierPerkRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/pledge-tier-perk').then(m => m.PledgeTierPerk),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/pledge-tier-perk-detail').then(m => m.PledgeTierPerkDetail),
    resolve: {
      pledgeTierPerk: PledgeTierPerkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/pledge-tier-perk-update').then(m => m.PledgeTierPerkUpdate),
    resolve: {
      pledgeTierPerk: PledgeTierPerkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/pledge-tier-perk-update').then(m => m.PledgeTierPerkUpdate),
    resolve: {
      pledgeTierPerk: PledgeTierPerkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default pledgeTierPerkRoute;
