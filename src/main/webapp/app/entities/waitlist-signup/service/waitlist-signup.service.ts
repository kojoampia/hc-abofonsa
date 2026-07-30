import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IWaitlistSignup, NewWaitlistSignup } from '../waitlist-signup.model';

export type PartialUpdateWaitlistSignup = Partial<IWaitlistSignup> & Pick<IWaitlistSignup, 'id'>;

type RestOf<T extends IWaitlistSignup | NewWaitlistSignup> = Omit<T, 'confirmedAt' | 'unsubscribedAt' | 'capturedAt'> & {
  confirmedAt?: string | null;
  unsubscribedAt?: string | null;
  capturedAt?: string | null;
};

export type RestWaitlistSignup = RestOf<IWaitlistSignup>;

export type NewRestWaitlistSignup = RestOf<NewWaitlistSignup>;

export type PartialUpdateRestWaitlistSignup = RestOf<PartialUpdateWaitlistSignup>;

@Injectable()
export class WaitlistSignupsService {
  readonly waitlistSignupsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly waitlistSignupsResource = httpResource<RestWaitlistSignup[]>(() => {
    const params = this.waitlistSignupsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of waitlistSignup that have been fetched. It is updated when the waitlistSignupsResource emits a new value.
   * In case of error while fetching the waitlistSignups, the signal is set to an empty array.
   */
  readonly waitlistSignups = computed(() =>
    (this.waitlistSignupsResource.hasValue() ? this.waitlistSignupsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/waitlist-signups');

  protected convertValueFromServer(restWaitlistSignup: RestWaitlistSignup): IWaitlistSignup {
    return {
      ...restWaitlistSignup,
      confirmedAt: restWaitlistSignup.confirmedAt ? dayjs(restWaitlistSignup.confirmedAt) : undefined,
      unsubscribedAt: restWaitlistSignup.unsubscribedAt ? dayjs(restWaitlistSignup.unsubscribedAt) : undefined,
      capturedAt: restWaitlistSignup.capturedAt ? dayjs(restWaitlistSignup.capturedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class WaitlistSignupService extends WaitlistSignupsService {
  protected readonly http = inject(HttpClient);

  create(waitlistSignup: NewWaitlistSignup): Observable<IWaitlistSignup> {
    const copy = this.convertValueFromClient(waitlistSignup);
    return this.http.post<RestWaitlistSignup>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(waitlistSignup: IWaitlistSignup): Observable<IWaitlistSignup> {
    const copy = this.convertValueFromClient(waitlistSignup);
    return this.http
      .put<RestWaitlistSignup>(`${this.resourceUrl}/${encodeURIComponent(this.getWaitlistSignupIdentifier(waitlistSignup))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(waitlistSignup: PartialUpdateWaitlistSignup): Observable<IWaitlistSignup> {
    const copy = this.convertValueFromClient(waitlistSignup);
    return this.http
      .patch<RestWaitlistSignup>(`${this.resourceUrl}/${encodeURIComponent(this.getWaitlistSignupIdentifier(waitlistSignup))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IWaitlistSignup> {
    return this.http
      .get<RestWaitlistSignup>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IWaitlistSignup[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWaitlistSignup[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getWaitlistSignupIdentifier(waitlistSignup: Pick<IWaitlistSignup, 'id'>): number {
    return waitlistSignup.id;
  }

  compareWaitlistSignup(o1: Pick<IWaitlistSignup, 'id'> | null, o2: Pick<IWaitlistSignup, 'id'> | null): boolean {
    return o1 && o2 ? this.getWaitlistSignupIdentifier(o1) === this.getWaitlistSignupIdentifier(o2) : o1 === o2;
  }

  addWaitlistSignupToCollectionIfMissing<Type extends Pick<IWaitlistSignup, 'id'>>(
    waitlistSignupCollection: Type[],
    ...waitlistSignupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const waitlistSignups: Type[] = waitlistSignupsToCheck.filter(isPresent);
    if (waitlistSignups.length > 0) {
      const waitlistSignupCollectionIdentifiers = waitlistSignupCollection.map(waitlistSignupItem =>
        this.getWaitlistSignupIdentifier(waitlistSignupItem),
      );
      const waitlistSignupsToAdd = waitlistSignups.filter(waitlistSignupItem => {
        const waitlistSignupIdentifier = this.getWaitlistSignupIdentifier(waitlistSignupItem);
        if (waitlistSignupCollectionIdentifiers.includes(waitlistSignupIdentifier)) {
          return false;
        }
        waitlistSignupCollectionIdentifiers.push(waitlistSignupIdentifier);
        return true;
      });
      return [...waitlistSignupsToAdd, ...waitlistSignupCollection];
    }
    return waitlistSignupCollection;
  }

  protected convertValueFromClient<T extends IWaitlistSignup | NewWaitlistSignup | PartialUpdateWaitlistSignup>(
    waitlistSignup: T,
  ): RestOf<T> {
    return {
      ...waitlistSignup,
      confirmedAt: waitlistSignup.confirmedAt?.toJSON() ?? null,
      unsubscribedAt: waitlistSignup.unsubscribedAt?.toJSON() ?? null,
      capturedAt: waitlistSignup.capturedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestWaitlistSignup): IWaitlistSignup {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestWaitlistSignup[]): IWaitlistSignup[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
