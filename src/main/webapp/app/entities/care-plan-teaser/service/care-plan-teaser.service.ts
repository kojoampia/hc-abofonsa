import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICarePlanTeaser, NewCarePlanTeaser } from '../care-plan-teaser.model';

export type PartialUpdateCarePlanTeaser = Partial<ICarePlanTeaser> & Pick<ICarePlanTeaser, 'id'>;

@Injectable()
export class CarePlanTeasersService {
  readonly carePlanTeasersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly carePlanTeasersResource = httpResource<ICarePlanTeaser[]>(() => {
    const params = this.carePlanTeasersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of carePlanTeaser that have been fetched. It is updated when the carePlanTeasersResource emits a new value.
   * In case of error while fetching the carePlanTeasers, the signal is set to an empty array.
   */
  readonly carePlanTeasers = computed(() => (this.carePlanTeasersResource.hasValue() ? this.carePlanTeasersResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/care-plan-teasers');
}

@Injectable({ providedIn: 'root' })
export class CarePlanTeaserService extends CarePlanTeasersService {
  protected readonly http = inject(HttpClient);

  create(carePlanTeaser: NewCarePlanTeaser): Observable<ICarePlanTeaser> {
    return this.http.post<ICarePlanTeaser>(this.resourceUrl, carePlanTeaser);
  }

  update(carePlanTeaser: ICarePlanTeaser): Observable<ICarePlanTeaser> {
    return this.http.put<ICarePlanTeaser>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCarePlanTeaserIdentifier(carePlanTeaser))}`,
      carePlanTeaser,
    );
  }

  partialUpdate(carePlanTeaser: PartialUpdateCarePlanTeaser): Observable<ICarePlanTeaser> {
    return this.http.patch<ICarePlanTeaser>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCarePlanTeaserIdentifier(carePlanTeaser))}`,
      carePlanTeaser,
    );
  }

  find(id: number): Observable<ICarePlanTeaser> {
    return this.http.get<ICarePlanTeaser>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICarePlanTeaser[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICarePlanTeaser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCarePlanTeaserIdentifier(carePlanTeaser: Pick<ICarePlanTeaser, 'id'>): number {
    return carePlanTeaser.id;
  }

  compareCarePlanTeaser(o1: Pick<ICarePlanTeaser, 'id'> | null, o2: Pick<ICarePlanTeaser, 'id'> | null): boolean {
    return o1 && o2 ? this.getCarePlanTeaserIdentifier(o1) === this.getCarePlanTeaserIdentifier(o2) : o1 === o2;
  }

  addCarePlanTeaserToCollectionIfMissing<Type extends Pick<ICarePlanTeaser, 'id'>>(
    carePlanTeaserCollection: Type[],
    ...carePlanTeasersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const carePlanTeasers: Type[] = carePlanTeasersToCheck.filter(isPresent);
    if (carePlanTeasers.length > 0) {
      const carePlanTeaserCollectionIdentifiers = carePlanTeaserCollection.map(carePlanTeaserItem =>
        this.getCarePlanTeaserIdentifier(carePlanTeaserItem),
      );
      const carePlanTeasersToAdd = carePlanTeasers.filter(carePlanTeaserItem => {
        const carePlanTeaserIdentifier = this.getCarePlanTeaserIdentifier(carePlanTeaserItem);
        if (carePlanTeaserCollectionIdentifiers.includes(carePlanTeaserIdentifier)) {
          return false;
        }
        carePlanTeaserCollectionIdentifiers.push(carePlanTeaserIdentifier);
        return true;
      });
      return [...carePlanTeasersToAdd, ...carePlanTeaserCollection];
    }
    return carePlanTeaserCollection;
  }
}
