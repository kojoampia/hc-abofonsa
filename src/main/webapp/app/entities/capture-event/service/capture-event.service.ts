import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICaptureEvent } from '../capture-event.model';

type RestOf<T extends ICaptureEvent> = Omit<T, 'occurredAt' | 'occurredDate'> & {
  occurredAt?: string | null;
  occurredDate?: string | null;
};

export type RestCaptureEvent = RestOf<ICaptureEvent>;

@Injectable()
export class CaptureEventsService {
  readonly captureEventsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly captureEventsResource = httpResource<RestCaptureEvent[]>(() => {
    const params = this.captureEventsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of captureEvent that have been fetched. It is updated when the captureEventsResource emits a new value.
   * In case of error while fetching the captureEvents, the signal is set to an empty array.
   */
  readonly captureEvents = computed(() =>
    (this.captureEventsResource.hasValue() ? this.captureEventsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/capture-events');

  protected convertValueFromServer(restCaptureEvent: RestCaptureEvent): ICaptureEvent {
    return {
      ...restCaptureEvent,
      occurredAt: restCaptureEvent.occurredAt ? dayjs(restCaptureEvent.occurredAt) : undefined,
      occurredDate: restCaptureEvent.occurredDate ? dayjs(restCaptureEvent.occurredDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CaptureEventService extends CaptureEventsService {
  protected readonly http = inject(HttpClient);

  find(id: number): Observable<ICaptureEvent> {
    return this.http
      .get<RestCaptureEvent>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICaptureEvent[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCaptureEvent[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  getCaptureEventIdentifier(captureEvent: Pick<ICaptureEvent, 'id'>): number {
    return captureEvent.id;
  }

  compareCaptureEvent(o1: Pick<ICaptureEvent, 'id'> | null, o2: Pick<ICaptureEvent, 'id'> | null): boolean {
    return o1 && o2 ? this.getCaptureEventIdentifier(o1) === this.getCaptureEventIdentifier(o2) : o1 === o2;
  }

  addCaptureEventToCollectionIfMissing<Type extends Pick<ICaptureEvent, 'id'>>(
    captureEventCollection: Type[],
    ...captureEventsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const captureEvents: Type[] = captureEventsToCheck.filter(isPresent);
    if (captureEvents.length > 0) {
      const captureEventCollectionIdentifiers = captureEventCollection.map(captureEventItem =>
        this.getCaptureEventIdentifier(captureEventItem),
      );
      const captureEventsToAdd = captureEvents.filter(captureEventItem => {
        const captureEventIdentifier = this.getCaptureEventIdentifier(captureEventItem);
        if (captureEventCollectionIdentifiers.includes(captureEventIdentifier)) {
          return false;
        }
        captureEventCollectionIdentifiers.push(captureEventIdentifier);
        return true;
      });
      return [...captureEventsToAdd, ...captureEventCollection];
    }
    return captureEventCollection;
  }

  protected convertValueFromClient<T extends ICaptureEvent>(captureEvent: T): RestOf<T> {
    return {
      ...captureEvent,
      occurredAt: captureEvent.occurredAt?.toJSON() ?? null,
      occurredDate: captureEvent.occurredDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCaptureEvent): ICaptureEvent {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCaptureEvent[]): ICaptureEvent[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
