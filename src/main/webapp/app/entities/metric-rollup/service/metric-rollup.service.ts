import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMetricRollup } from '../metric-rollup.model';

type RestOf<T extends IMetricRollup> = Omit<T, 'bucketStart' | 'bucketEnd' | 'computedAt'> & {
  bucketStart?: string | null;
  bucketEnd?: string | null;
  computedAt?: string | null;
};

export type RestMetricRollup = RestOf<IMetricRollup>;

@Injectable()
export class MetricRollupsService {
  readonly metricRollupsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly metricRollupsResource = httpResource<RestMetricRollup[]>(() => {
    const params = this.metricRollupsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of metricRollup that have been fetched. It is updated when the metricRollupsResource emits a new value.
   * In case of error while fetching the metricRollups, the signal is set to an empty array.
   */
  readonly metricRollups = computed(() =>
    (this.metricRollupsResource.hasValue() ? this.metricRollupsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/metric-rollups');

  protected convertValueFromServer(restMetricRollup: RestMetricRollup): IMetricRollup {
    return {
      ...restMetricRollup,
      bucketStart: restMetricRollup.bucketStart ? dayjs(restMetricRollup.bucketStart) : undefined,
      bucketEnd: restMetricRollup.bucketEnd ? dayjs(restMetricRollup.bucketEnd) : undefined,
      computedAt: restMetricRollup.computedAt ? dayjs(restMetricRollup.computedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class MetricRollupService extends MetricRollupsService {
  protected readonly http = inject(HttpClient);

  find(id: number): Observable<IMetricRollup> {
    return this.http
      .get<RestMetricRollup>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IMetricRollup[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMetricRollup[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  getMetricRollupIdentifier(metricRollup: Pick<IMetricRollup, 'id'>): number {
    return metricRollup.id;
  }

  compareMetricRollup(o1: Pick<IMetricRollup, 'id'> | null, o2: Pick<IMetricRollup, 'id'> | null): boolean {
    return o1 && o2 ? this.getMetricRollupIdentifier(o1) === this.getMetricRollupIdentifier(o2) : o1 === o2;
  }

  addMetricRollupToCollectionIfMissing<Type extends Pick<IMetricRollup, 'id'>>(
    metricRollupCollection: Type[],
    ...metricRollupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const metricRollups: Type[] = metricRollupsToCheck.filter(isPresent);
    if (metricRollups.length > 0) {
      const metricRollupCollectionIdentifiers = metricRollupCollection.map(metricRollupItem =>
        this.getMetricRollupIdentifier(metricRollupItem),
      );
      const metricRollupsToAdd = metricRollups.filter(metricRollupItem => {
        const metricRollupIdentifier = this.getMetricRollupIdentifier(metricRollupItem);
        if (metricRollupCollectionIdentifiers.includes(metricRollupIdentifier)) {
          return false;
        }
        metricRollupCollectionIdentifiers.push(metricRollupIdentifier);
        return true;
      });
      return [...metricRollupsToAdd, ...metricRollupCollection];
    }
    return metricRollupCollection;
  }

  protected convertValueFromClient<T extends IMetricRollup>(metricRollup: T): RestOf<T> {
    return {
      ...metricRollup,
      bucketStart: metricRollup.bucketStart?.toJSON() ?? null,
      bucketEnd: metricRollup.bucketEnd?.toJSON() ?? null,
      computedAt: metricRollup.computedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestMetricRollup): IMetricRollup {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestMetricRollup[]): IMetricRollup[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
