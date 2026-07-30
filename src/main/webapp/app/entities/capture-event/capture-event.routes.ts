import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CaptureEventResolve from './route/capture-event-routing-resolve.service';

const captureEventRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/capture-event').then(m => m.CaptureEvent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/capture-event-detail').then(m => m.CaptureEventDetail),
    resolve: {
      captureEvent: CaptureEventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default captureEventRoute;
