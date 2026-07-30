import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPledgeTierPerk, NewPledgeTierPerk } from '../pledge-tier-perk.model';

export type PartialUpdatePledgeTierPerk = Partial<IPledgeTierPerk> & Pick<IPledgeTierPerk, 'id'>;

@Injectable()
export class PledgeTierPerksService {
  readonly pledgeTierPerksParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly pledgeTierPerksResource = httpResource<IPledgeTierPerk[]>(() => {
    const params = this.pledgeTierPerksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of pledgeTierPerk that have been fetched. It is updated when the pledgeTierPerksResource emits a new value.
   * In case of error while fetching the pledgeTierPerks, the signal is set to an empty array.
   */
  readonly pledgeTierPerks = computed(() => (this.pledgeTierPerksResource.hasValue() ? this.pledgeTierPerksResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/pledge-tier-perks');
}

@Injectable({ providedIn: 'root' })
export class PledgeTierPerkService extends PledgeTierPerksService {
  protected readonly http = inject(HttpClient);

  create(pledgeTierPerk: NewPledgeTierPerk): Observable<IPledgeTierPerk> {
    return this.http.post<IPledgeTierPerk>(this.resourceUrl, pledgeTierPerk);
  }

  update(pledgeTierPerk: IPledgeTierPerk): Observable<IPledgeTierPerk> {
    return this.http.put<IPledgeTierPerk>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPledgeTierPerkIdentifier(pledgeTierPerk))}`,
      pledgeTierPerk,
    );
  }

  partialUpdate(pledgeTierPerk: PartialUpdatePledgeTierPerk): Observable<IPledgeTierPerk> {
    return this.http.patch<IPledgeTierPerk>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPledgeTierPerkIdentifier(pledgeTierPerk))}`,
      pledgeTierPerk,
    );
  }

  find(id: number): Observable<IPledgeTierPerk> {
    return this.http.get<IPledgeTierPerk>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPledgeTierPerk[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPledgeTierPerk[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPledgeTierPerkIdentifier(pledgeTierPerk: Pick<IPledgeTierPerk, 'id'>): number {
    return pledgeTierPerk.id;
  }

  comparePledgeTierPerk(o1: Pick<IPledgeTierPerk, 'id'> | null, o2: Pick<IPledgeTierPerk, 'id'> | null): boolean {
    return o1 && o2 ? this.getPledgeTierPerkIdentifier(o1) === this.getPledgeTierPerkIdentifier(o2) : o1 === o2;
  }

  addPledgeTierPerkToCollectionIfMissing<Type extends Pick<IPledgeTierPerk, 'id'>>(
    pledgeTierPerkCollection: Type[],
    ...pledgeTierPerksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const pledgeTierPerks: Type[] = pledgeTierPerksToCheck.filter(isPresent);
    if (pledgeTierPerks.length > 0) {
      const pledgeTierPerkCollectionIdentifiers = pledgeTierPerkCollection.map(pledgeTierPerkItem =>
        this.getPledgeTierPerkIdentifier(pledgeTierPerkItem),
      );
      const pledgeTierPerksToAdd = pledgeTierPerks.filter(pledgeTierPerkItem => {
        const pledgeTierPerkIdentifier = this.getPledgeTierPerkIdentifier(pledgeTierPerkItem);
        if (pledgeTierPerkCollectionIdentifiers.includes(pledgeTierPerkIdentifier)) {
          return false;
        }
        pledgeTierPerkCollectionIdentifiers.push(pledgeTierPerkIdentifier);
        return true;
      });
      return [...pledgeTierPerksToAdd, ...pledgeTierPerkCollection];
    }
    return pledgeTierPerkCollection;
  }
}
