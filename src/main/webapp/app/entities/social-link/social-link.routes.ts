import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SocialLinkResolve from './route/social-link-routing-resolve.service';

const socialLinkRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/social-link').then(m => m.SocialLink),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/social-link-detail').then(m => m.SocialLinkDetail),
    resolve: {
      socialLink: SocialLinkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/social-link-update').then(m => m.SocialLinkUpdate),
    resolve: {
      socialLink: SocialLinkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/social-link-update').then(m => m.SocialLinkUpdate),
    resolve: {
      socialLink: SocialLinkResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default socialLinkRoute;
