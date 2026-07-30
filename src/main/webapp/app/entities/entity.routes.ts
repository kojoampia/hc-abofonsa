import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'abofonsaPreviewApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'waitlist-signup',
    data: { pageTitle: 'abofonsaPreviewApp.waitlistSignup.home.title' },
    loadChildren: () => import('./waitlist-signup/waitlist-signup.routes'),
  },
  {
    path: 'capture-event',
    data: { pageTitle: 'abofonsaPreviewApp.captureEvent.home.title' },
    loadChildren: () => import('./capture-event/capture-event.routes'),
  },
  {
    path: 'metric-rollup',
    data: { pageTitle: 'abofonsaPreviewApp.metricRollup.home.title' },
    loadChildren: () => import('./metric-rollup/metric-rollup.routes'),
  },
  {
    path: 'data-export-log',
    data: { pageTitle: 'abofonsaPreviewApp.dataExportLog.home.title' },
    loadChildren: () => import('./data-export-log/data-export-log.routes'),
  },
  {
    path: 'care-service-teaser',
    data: { pageTitle: 'abofonsaPreviewApp.careServiceTeaser.home.title' },
    loadChildren: () => import('./care-service-teaser/care-service-teaser.routes'),
  },
  {
    path: 'service-highlight',
    data: { pageTitle: 'abofonsaPreviewApp.serviceHighlight.home.title' },
    loadChildren: () => import('./service-highlight/service-highlight.routes'),
  },
  {
    path: 'care-plan-teaser',
    data: { pageTitle: 'abofonsaPreviewApp.carePlanTeaser.home.title' },
    loadChildren: () => import('./care-plan-teaser/care-plan-teaser.routes'),
  },
  {
    path: 'plan-feature',
    data: { pageTitle: 'abofonsaPreviewApp.planFeature.home.title' },
    loadChildren: () => import('./plan-feature/plan-feature.routes'),
  },
  {
    path: 'pledge-tier-teaser',
    data: { pageTitle: 'abofonsaPreviewApp.pledgeTierTeaser.home.title' },
    loadChildren: () => import('./pledge-tier-teaser/pledge-tier-teaser.routes'),
  },
  {
    path: 'pledge-tier-perk',
    data: { pageTitle: 'abofonsaPreviewApp.pledgeTierPerk.home.title' },
    loadChildren: () => import('./pledge-tier-perk/pledge-tier-perk.routes'),
  },
  {
    path: 'launch-milestone',
    data: { pageTitle: 'abofonsaPreviewApp.launchMilestone.home.title' },
    loadChildren: () => import('./launch-milestone/launch-milestone.routes'),
  },
  {
    path: 'social-link',
    data: { pageTitle: 'abofonsaPreviewApp.socialLink.home.title' },
    loadChildren: () => import('./social-link/social-link.routes'),
  },
  {
    path: 'launch-setting',
    data: { pageTitle: 'abofonsaPreviewApp.launchSetting.home.title' },
    loadChildren: () => import('./launch-setting/launch-setting.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
