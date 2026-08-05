import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  {
    // The launch page replaces JHipster's home. `chrome: false` tells the main layout to stand
    // aside: the navbar, footer and card container belong to the admin application, and the public
    // page is a full-bleed design that must not be nested inside them.
    path: '',
    loadComponent: () => import('./launch/launch.page'),
    title: 'launch.title',
    data: { chrome: false },
  },
  // Where the links in an opt-in email land. `/confirm` and `/unsubscribe` ask and then post;
  // `/confirmed` and `/unsubscribed` are the results, kept because older messages may point at them.
  {
    path: 'confirm',
    loadComponent: () => import('./launch/opt-in-action.page'),
    title: 'launch.optIn.confirmed.title',
    data: { chrome: false, kind: 'confirm' },
  },
  {
    path: 'unsubscribe',
    loadComponent: () => import('./launch/opt-in-action.page'),
    title: 'launch.optIn.unsubscribed.title',
    data: { chrome: false, kind: 'unsubscribe' },
  },
  {
    path: 'confirmed',
    loadComponent: () => import('./launch/opt-in-result.page'),
    title: 'launch.optIn.confirmed.title',
    data: { chrome: false, kind: 'confirmed' },
  },
  {
    path: 'unsubscribed',
    loadComponent: () => import('./launch/opt-in-result.page'),
    title: 'launch.optIn.unsubscribed.title',
    data: { chrome: false, kind: 'unsubscribed' },
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar'),
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login'),
    title: 'login.title',
  },
  {
    path: '',
    loadChildren: () => import('./entities/entity.routes'),
  },
  ...errorRoute,
];

export default routes;
