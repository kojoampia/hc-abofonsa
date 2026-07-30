import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPledgeTierTeaser, NewPledgeTierTeaser } from '../pledge-tier-teaser.model';

export type PartialUpdatePledgeTierTeaser = Partial<IPledgeTierTeaser> & Pick<IPledgeTierTeaser, 'id'>;

@Injectable()
export class PledgeTierTeasersService {
  readonly pledgeTierTeasersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly pledgeTierTeasersResource = httpResource<IPledgeTierTeaser[]>(() => {
    const params = this.pledgeTierTeasersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of pledgeTierTeaser that have been fetched. It is updated when the pledgeTierTeasersResource emits a new value.
   * In case of error while fetching the pledgeTierTeasers, the signal is set to an empty array.
   */
  readonly pledgeTierTeasers = computed(() => (this.pledgeTierTeasersResource.hasValue() ? this.pledgeTierTeasersResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/pledge-tier-teasers');
}

@Injectable({ providedIn: 'root' })
export class PledgeTierTeaserService extends PledgeTierTeasersService {
  protected readonly http = inject(HttpClient);

  create(pledgeTierTeaser: NewPledgeTierTeaser): Observable<IPledgeTierTeaser> {
    return this.http.post<IPledgeTierTeaser>(this.resourceUrl, pledgeTierTeaser);
  }

  update(pledgeTierTeaser: IPledgeTierTeaser): Observable<IPledgeTierTeaser> {
    return this.http.put<IPledgeTierTeaser>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPledgeTierTeaserIdentifier(pledgeTierTeaser))}`,
      pledgeTierTeaser,
    );
  }

  partialUpdate(pledgeTierTeaser: PartialUpdatePledgeTierTeaser): Observable<IPledgeTierTeaser> {
    return this.http.patch<IPledgeTierTeaser>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPledgeTierTeaserIdentifier(pledgeTierTeaser))}`,
      pledgeTierTeaser,
    );
  }

  find(id: number): Observable<IPledgeTierTeaser> {
    return this.http.get<IPledgeTierTeaser>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPledgeTierTeaser[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPledgeTierTeaser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPledgeTierTeaserIdentifier(pledgeTierTeaser: Pick<IPledgeTierTeaser, 'id'>): number {
    return pledgeTierTeaser.id;
  }

  comparePledgeTierTeaser(o1: Pick<IPledgeTierTeaser, 'id'> | null, o2: Pick<IPledgeTierTeaser, 'id'> | null): boolean {
    return o1 && o2 ? this.getPledgeTierTeaserIdentifier(o1) === this.getPledgeTierTeaserIdentifier(o2) : o1 === o2;
  }

  addPledgeTierTeaserToCollectionIfMissing<Type extends Pick<IPledgeTierTeaser, 'id'>>(
    pledgeTierTeaserCollection: Type[],
    ...pledgeTierTeasersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const pledgeTierTeasers: Type[] = pledgeTierTeasersToCheck.filter(isPresent);
    if (pledgeTierTeasers.length > 0) {
      const pledgeTierTeaserCollectionIdentifiers = pledgeTierTeaserCollection.map(pledgeTierTeaserItem =>
        this.getPledgeTierTeaserIdentifier(pledgeTierTeaserItem),
      );
      const pledgeTierTeasersToAdd = pledgeTierTeasers.filter(pledgeTierTeaserItem => {
        const pledgeTierTeaserIdentifier = this.getPledgeTierTeaserIdentifier(pledgeTierTeaserItem);
        if (pledgeTierTeaserCollectionIdentifiers.includes(pledgeTierTeaserIdentifier)) {
          return false;
        }
        pledgeTierTeaserCollectionIdentifiers.push(pledgeTierTeaserIdentifier);
        return true;
      });
      return [...pledgeTierTeasersToAdd, ...pledgeTierTeaserCollection];
    }
    return pledgeTierTeaserCollection;
  }
}
