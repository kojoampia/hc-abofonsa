import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPledgeTierPerk } from '../pledge-tier-perk.model';
import { PledgeTierPerkService } from '../service/pledge-tier-perk.service';

const pledgeTierPerkResolve = (route: ActivatedRouteSnapshot): Observable<null | IPledgeTierPerk> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PledgeTierPerkService);
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

export default pledgeTierPerkResolve;
