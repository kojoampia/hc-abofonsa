import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IMetricRollup } from '../metric-rollup.model';
import { MetricRollupService } from '../service/metric-rollup.service';

const metricRollupResolve = (route: ActivatedRouteSnapshot): Observable<null | IMetricRollup> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(MetricRollupService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default metricRollupResolve;
