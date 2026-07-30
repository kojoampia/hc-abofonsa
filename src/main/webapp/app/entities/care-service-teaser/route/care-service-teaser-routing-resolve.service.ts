import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICareServiceTeaser } from '../care-service-teaser.model';
import { CareServiceTeaserService } from '../service/care-service-teaser.service';

const careServiceTeaserResolve = (route: ActivatedRouteSnapshot): Observable<null | ICareServiceTeaser> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CareServiceTeaserService);
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

export default careServiceTeaserResolve;
