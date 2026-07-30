import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICaptureEvent } from '../capture-event.model';
import { CaptureEventService } from '../service/capture-event.service';

const captureEventResolve = (route: ActivatedRouteSnapshot): Observable<null | ICaptureEvent> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CaptureEventService);
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

export default captureEventResolve;
