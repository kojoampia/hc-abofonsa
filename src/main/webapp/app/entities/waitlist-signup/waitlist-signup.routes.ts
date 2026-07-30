import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import WaitlistSignupResolve from './route/waitlist-signup-routing-resolve.service';

const waitlistSignupRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/waitlist-signup').then(m => m.WaitlistSignup),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/waitlist-signup-detail').then(m => m.WaitlistSignupDetail),
    resolve: {
      waitlistSignup: WaitlistSignupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/waitlist-signup-update').then(m => m.WaitlistSignupUpdate),
    resolve: {
      waitlistSignup: WaitlistSignupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/waitlist-signup-update').then(m => m.WaitlistSignupUpdate),
    resolve: {
      waitlistSignup: WaitlistSignupResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default waitlistSignupRoute;
