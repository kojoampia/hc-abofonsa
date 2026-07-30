import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ServiceHighlightService } from '../service/service-highlight.service';
import { IServiceHighlight } from '../service-highlight.model';

const serviceHighlightResolve = (route: ActivatedRouteSnapshot): Observable<null | IServiceHighlight> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(ServiceHighlightService);
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

export default serviceHighlightResolve;
