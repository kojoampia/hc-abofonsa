import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IServiceHighlight, NewServiceHighlight } from '../service-highlight.model';

export type PartialUpdateServiceHighlight = Partial<IServiceHighlight> & Pick<IServiceHighlight, 'id'>;

@Injectable()
export class ServiceHighlightsService {
  readonly serviceHighlightsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly serviceHighlightsResource = httpResource<IServiceHighlight[]>(() => {
    const params = this.serviceHighlightsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of serviceHighlight that have been fetched. It is updated when the serviceHighlightsResource emits a new value.
   * In case of error while fetching the serviceHighlights, the signal is set to an empty array.
   */
  readonly serviceHighlights = computed(() => (this.serviceHighlightsResource.hasValue() ? this.serviceHighlightsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/service-highlights');
}

@Injectable({ providedIn: 'root' })
export class ServiceHighlightService extends ServiceHighlightsService {
  protected readonly http = inject(HttpClient);

  create(serviceHighlight: NewServiceHighlight): Observable<IServiceHighlight> {
    return this.http.post<IServiceHighlight>(this.resourceUrl, serviceHighlight);
  }

  update(serviceHighlight: IServiceHighlight): Observable<IServiceHighlight> {
    return this.http.put<IServiceHighlight>(
      `${this.resourceUrl}/${encodeURIComponent(this.getServiceHighlightIdentifier(serviceHighlight))}`,
      serviceHighlight,
    );
  }

  partialUpdate(serviceHighlight: PartialUpdateServiceHighlight): Observable<IServiceHighlight> {
    return this.http.patch<IServiceHighlight>(
      `${this.resourceUrl}/${encodeURIComponent(this.getServiceHighlightIdentifier(serviceHighlight))}`,
      serviceHighlight,
    );
  }

  find(id: number): Observable<IServiceHighlight> {
    return this.http.get<IServiceHighlight>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IServiceHighlight[]>> {
    const options = createRequestOption(req);
    return this.http.get<IServiceHighlight[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getServiceHighlightIdentifier(serviceHighlight: Pick<IServiceHighlight, 'id'>): number {
    return serviceHighlight.id;
  }

  compareServiceHighlight(o1: Pick<IServiceHighlight, 'id'> | null, o2: Pick<IServiceHighlight, 'id'> | null): boolean {
    return o1 && o2 ? this.getServiceHighlightIdentifier(o1) === this.getServiceHighlightIdentifier(o2) : o1 === o2;
  }

  addServiceHighlightToCollectionIfMissing<Type extends Pick<IServiceHighlight, 'id'>>(
    serviceHighlightCollection: Type[],
    ...serviceHighlightsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const serviceHighlights: Type[] = serviceHighlightsToCheck.filter(isPresent);
    if (serviceHighlights.length > 0) {
      const serviceHighlightCollectionIdentifiers = serviceHighlightCollection.map(serviceHighlightItem =>
        this.getServiceHighlightIdentifier(serviceHighlightItem),
      );
      const serviceHighlightsToAdd = serviceHighlights.filter(serviceHighlightItem => {
        const serviceHighlightIdentifier = this.getServiceHighlightIdentifier(serviceHighlightItem);
        if (serviceHighlightCollectionIdentifiers.includes(serviceHighlightIdentifier)) {
          return false;
        }
        serviceHighlightCollectionIdentifiers.push(serviceHighlightIdentifier);
        return true;
      });
      return [...serviceHighlightsToAdd, ...serviceHighlightCollection];
    }
    return serviceHighlightCollection;
  }
}
