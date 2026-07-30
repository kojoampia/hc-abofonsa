import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import DataExportLogResolve from './route/data-export-log-routing-resolve.service';

const dataExportLogRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/data-export-log').then(m => m.DataExportLog),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/data-export-log-detail').then(m => m.DataExportLogDetail),
    resolve: {
      dataExportLog: DataExportLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default dataExportLogRoute;
