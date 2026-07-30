import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LaunchSettingResolve from './route/launch-setting-routing-resolve.service';

const launchSettingRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/launch-setting').then(m => m.LaunchSetting),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/launch-setting-detail').then(m => m.LaunchSettingDetail),
    resolve: {
      launchSetting: LaunchSettingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/launch-setting-update').then(m => m.LaunchSettingUpdate),
    resolve: {
      launchSetting: LaunchSettingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/launch-setting-update').then(m => m.LaunchSettingUpdate),
    resolve: {
      launchSetting: LaunchSettingResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default launchSettingRoute;
