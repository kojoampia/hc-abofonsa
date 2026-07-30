import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { WaitlistSignupService } from '../service/waitlist-signup.service';
import { IWaitlistSignup } from '../waitlist-signup.model';

const waitlistSignupResolve = (route: ActivatedRouteSnapshot): Observable<null | IWaitlistSignup> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(WaitlistSignupService);
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

export default waitlistSignupResolve;
