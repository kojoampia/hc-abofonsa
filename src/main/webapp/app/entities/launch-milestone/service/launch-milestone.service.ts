import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILaunchMilestone, NewLaunchMilestone } from '../launch-milestone.model';

export type PartialUpdateLaunchMilestone = Partial<ILaunchMilestone> & Pick<ILaunchMilestone, 'id'>;

type RestOf<T extends ILaunchMilestone | NewLaunchMilestone> = Omit<T, 'milestoneDate'> & {
  milestoneDate?: string | null;
};

export type RestLaunchMilestone = RestOf<ILaunchMilestone>;

export type NewRestLaunchMilestone = RestOf<NewLaunchMilestone>;

export type PartialUpdateRestLaunchMilestone = RestOf<PartialUpdateLaunchMilestone>;

@Injectable()
export class LaunchMilestonesService {
  readonly launchMilestonesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly launchMilestonesResource = httpResource<RestLaunchMilestone[]>(() => {
    const params = this.launchMilestonesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of launchMilestone that have been fetched. It is updated when the launchMilestonesResource emits a new value.
   * In case of error while fetching the launchMilestones, the signal is set to an empty array.
   */
  readonly launchMilestones = computed(() =>
    (this.launchMilestonesResource.hasValue() ? this.launchMilestonesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/launch-milestones');

  protected convertValueFromServer(restLaunchMilestone: RestLaunchMilestone): ILaunchMilestone {
    return {
      ...restLaunchMilestone,
      milestoneDate: restLaunchMilestone.milestoneDate ? dayjs(restLaunchMilestone.milestoneDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class LaunchMilestoneService extends LaunchMilestonesService {
  protected readonly http = inject(HttpClient);

  create(launchMilestone: NewLaunchMilestone): Observable<ILaunchMilestone> {
    const copy = this.convertValueFromClient(launchMilestone);
    return this.http.post<RestLaunchMilestone>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(launchMilestone: ILaunchMilestone): Observable<ILaunchMilestone> {
    const copy = this.convertValueFromClient(launchMilestone);
    return this.http
      .put<RestLaunchMilestone>(`${this.resourceUrl}/${encodeURIComponent(this.getLaunchMilestoneIdentifier(launchMilestone))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(launchMilestone: PartialUpdateLaunchMilestone): Observable<ILaunchMilestone> {
    const copy = this.convertValueFromClient(launchMilestone);
    return this.http
      .patch<RestLaunchMilestone>(`${this.resourceUrl}/${encodeURIComponent(this.getLaunchMilestoneIdentifier(launchMilestone))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ILaunchMilestone> {
    return this.http
      .get<RestLaunchMilestone>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ILaunchMilestone[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLaunchMilestone[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLaunchMilestoneIdentifier(launchMilestone: Pick<ILaunchMilestone, 'id'>): number {
    return launchMilestone.id;
  }

  compareLaunchMilestone(o1: Pick<ILaunchMilestone, 'id'> | null, o2: Pick<ILaunchMilestone, 'id'> | null): boolean {
    return o1 && o2 ? this.getLaunchMilestoneIdentifier(o1) === this.getLaunchMilestoneIdentifier(o2) : o1 === o2;
  }

  addLaunchMilestoneToCollectionIfMissing<Type extends Pick<ILaunchMilestone, 'id'>>(
    launchMilestoneCollection: Type[],
    ...launchMilestonesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const launchMilestones: Type[] = launchMilestonesToCheck.filter(isPresent);
    if (launchMilestones.length > 0) {
      const launchMilestoneCollectionIdentifiers = launchMilestoneCollection.map(launchMilestoneItem =>
        this.getLaunchMilestoneIdentifier(launchMilestoneItem),
      );
      const launchMilestonesToAdd = launchMilestones.filter(launchMilestoneItem => {
        const launchMilestoneIdentifier = this.getLaunchMilestoneIdentifier(launchMilestoneItem);
        if (launchMilestoneCollectionIdentifiers.includes(launchMilestoneIdentifier)) {
          return false;
        }
        launchMilestoneCollectionIdentifiers.push(launchMilestoneIdentifier);
        return true;
      });
      return [...launchMilestonesToAdd, ...launchMilestoneCollection];
    }
    return launchMilestoneCollection;
  }

  protected convertValueFromClient<T extends ILaunchMilestone | NewLaunchMilestone | PartialUpdateLaunchMilestone>(
    launchMilestone: T,
  ): RestOf<T> {
    return {
      ...launchMilestone,
      milestoneDate: launchMilestone.milestoneDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestLaunchMilestone): ILaunchMilestone {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestLaunchMilestone[]): ILaunchMilestone[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
