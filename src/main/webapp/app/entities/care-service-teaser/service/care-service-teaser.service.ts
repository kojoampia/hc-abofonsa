import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICareServiceTeaser, NewCareServiceTeaser } from '../care-service-teaser.model';

export type PartialUpdateCareServiceTeaser = Partial<ICareServiceTeaser> & Pick<ICareServiceTeaser, 'id'>;

@Injectable()
export class CareServiceTeasersService {
  readonly careServiceTeasersParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly careServiceTeasersResource = httpResource<ICareServiceTeaser[]>(() => {
    const params = this.careServiceTeasersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of careServiceTeaser that have been fetched. It is updated when the careServiceTeasersResource emits a new value.
   * In case of error while fetching the careServiceTeasers, the signal is set to an empty array.
   */
  readonly careServiceTeasers = computed(() => (this.careServiceTeasersResource.hasValue() ? this.careServiceTeasersResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/care-service-teasers');
}

@Injectable({ providedIn: 'root' })
export class CareServiceTeaserService extends CareServiceTeasersService {
  protected readonly http = inject(HttpClient);

  create(careServiceTeaser: NewCareServiceTeaser): Observable<ICareServiceTeaser> {
    return this.http.post<ICareServiceTeaser>(this.resourceUrl, careServiceTeaser);
  }

  update(careServiceTeaser: ICareServiceTeaser): Observable<ICareServiceTeaser> {
    return this.http.put<ICareServiceTeaser>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCareServiceTeaserIdentifier(careServiceTeaser))}`,
      careServiceTeaser,
    );
  }

  partialUpdate(careServiceTeaser: PartialUpdateCareServiceTeaser): Observable<ICareServiceTeaser> {
    return this.http.patch<ICareServiceTeaser>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCareServiceTeaserIdentifier(careServiceTeaser))}`,
      careServiceTeaser,
    );
  }

  find(id: number): Observable<ICareServiceTeaser> {
    return this.http.get<ICareServiceTeaser>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICareServiceTeaser[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICareServiceTeaser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCareServiceTeaserIdentifier(careServiceTeaser: Pick<ICareServiceTeaser, 'id'>): number {
    return careServiceTeaser.id;
  }

  compareCareServiceTeaser(o1: Pick<ICareServiceTeaser, 'id'> | null, o2: Pick<ICareServiceTeaser, 'id'> | null): boolean {
    return o1 && o2 ? this.getCareServiceTeaserIdentifier(o1) === this.getCareServiceTeaserIdentifier(o2) : o1 === o2;
  }

  addCareServiceTeaserToCollectionIfMissing<Type extends Pick<ICareServiceTeaser, 'id'>>(
    careServiceTeaserCollection: Type[],
    ...careServiceTeasersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const careServiceTeasers: Type[] = careServiceTeasersToCheck.filter(isPresent);
    if (careServiceTeasers.length > 0) {
      const careServiceTeaserCollectionIdentifiers = careServiceTeaserCollection.map(careServiceTeaserItem =>
        this.getCareServiceTeaserIdentifier(careServiceTeaserItem),
      );
      const careServiceTeasersToAdd = careServiceTeasers.filter(careServiceTeaserItem => {
        const careServiceTeaserIdentifier = this.getCareServiceTeaserIdentifier(careServiceTeaserItem);
        if (careServiceTeaserCollectionIdentifiers.includes(careServiceTeaserIdentifier)) {
          return false;
        }
        careServiceTeaserCollectionIdentifiers.push(careServiceTeaserIdentifier);
        return true;
      });
      return [...careServiceTeasersToAdd, ...careServiceTeaserCollection];
    }
    return careServiceTeaserCollection;
  }
}
