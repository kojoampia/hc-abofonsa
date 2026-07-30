import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LaunchMilestoneResolve from './route/launch-milestone-routing-resolve.service';

const launchMilestoneRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/launch-milestone').then(m => m.LaunchMilestone),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/launch-milestone-detail').then(m => m.LaunchMilestoneDetail),
    resolve: {
      launchMilestone: LaunchMilestoneResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/launch-milestone-update').then(m => m.LaunchMilestoneUpdate),
    resolve: {
      launchMilestone: LaunchMilestoneResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/launch-milestone-update').then(m => m.LaunchMilestoneUpdate),
    resolve: {
      launchMilestone: LaunchMilestoneResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default launchMilestoneRoute;
