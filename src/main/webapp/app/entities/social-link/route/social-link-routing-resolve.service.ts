import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { SocialLinkService } from '../service/social-link.service';
import { ISocialLink } from '../social-link.model';

const socialLinkResolve = (route: ActivatedRouteSnapshot): Observable<null | ISocialLink> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(SocialLinkService);
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

export default socialLinkResolve;
