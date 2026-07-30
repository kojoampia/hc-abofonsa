import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPledgeTierTeaser } from '../pledge-tier-teaser.model';
import { PledgeTierTeaserService } from '../service/pledge-tier-teaser.service';

const pledgeTierTeaserResolve = (route: ActivatedRouteSnapshot): Observable<null | IPledgeTierTeaser> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PledgeTierTeaserService);
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

export default pledgeTierTeaserResolve;
