import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICarePlanTeaser } from '../care-plan-teaser.model';
import { CarePlanTeaserService } from '../service/care-plan-teaser.service';

const carePlanTeaserResolve = (route: ActivatedRouteSnapshot): Observable<null | ICarePlanTeaser> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CarePlanTeaserService);
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

export default carePlanTeaserResolve;
