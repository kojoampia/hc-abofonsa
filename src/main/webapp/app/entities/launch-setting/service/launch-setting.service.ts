import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILaunchSetting, NewLaunchSetting } from '../launch-setting.model';

export type PartialUpdateLaunchSetting = Partial<ILaunchSetting> & Pick<ILaunchSetting, 'id'>;

type RestOf<T extends ILaunchSetting | NewLaunchSetting> = Omit<T, 'launchAt'> & {
  launchAt?: string | null;
};

export type RestLaunchSetting = RestOf<ILaunchSetting>;

export type NewRestLaunchSetting = RestOf<NewLaunchSetting>;

export type PartialUpdateRestLaunchSetting = RestOf<PartialUpdateLaunchSetting>;

@Injectable()
export class LaunchSettingsService {
  readonly launchSettingsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly launchSettingsResource = httpResource<RestLaunchSetting[]>(() => {
    const params = this.launchSettingsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of launchSetting that have been fetched. It is updated when the launchSettingsResource emits a new value.
   * In case of error while fetching the launchSettings, the signal is set to an empty array.
   */
  readonly launchSettings = computed(() =>
    (this.launchSettingsResource.hasValue() ? this.launchSettingsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/launch-settings');

  protected convertValueFromServer(restLaunchSetting: RestLaunchSetting): ILaunchSetting {
    return {
      ...restLaunchSetting,
      launchAt: restLaunchSetting.launchAt ? dayjs(restLaunchSetting.launchAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class LaunchSettingService extends LaunchSettingsService {
  protected readonly http = inject(HttpClient);

  create(launchSetting: NewLaunchSetting): Observable<ILaunchSetting> {
    const copy = this.convertValueFromClient(launchSetting);
    return this.http.post<RestLaunchSetting>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(launchSetting: ILaunchSetting): Observable<ILaunchSetting> {
    const copy = this.convertValueFromClient(launchSetting);
    return this.http
      .put<RestLaunchSetting>(`${this.resourceUrl}/${encodeURIComponent(this.getLaunchSettingIdentifier(launchSetting))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(launchSetting: PartialUpdateLaunchSetting): Observable<ILaunchSetting> {
    const copy = this.convertValueFromClient(launchSetting);
    return this.http
      .patch<RestLaunchSetting>(`${this.resourceUrl}/${encodeURIComponent(this.getLaunchSettingIdentifier(launchSetting))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ILaunchSetting> {
    return this.http
      .get<RestLaunchSetting>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ILaunchSetting[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLaunchSetting[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLaunchSettingIdentifier(launchSetting: Pick<ILaunchSetting, 'id'>): number {
    return launchSetting.id;
  }

  compareLaunchSetting(o1: Pick<ILaunchSetting, 'id'> | null, o2: Pick<ILaunchSetting, 'id'> | null): boolean {
    return o1 && o2 ? this.getLaunchSettingIdentifier(o1) === this.getLaunchSettingIdentifier(o2) : o1 === o2;
  }

  addLaunchSettingToCollectionIfMissing<Type extends Pick<ILaunchSetting, 'id'>>(
    launchSettingCollection: Type[],
    ...launchSettingsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const launchSettings: Type[] = launchSettingsToCheck.filter(isPresent);
    if (launchSettings.length > 0) {
      const launchSettingCollectionIdentifiers = launchSettingCollection.map(launchSettingItem =>
        this.getLaunchSettingIdentifier(launchSettingItem),
      );
      const launchSettingsToAdd = launchSettings.filter(launchSettingItem => {
        const launchSettingIdentifier = this.getLaunchSettingIdentifier(launchSettingItem);
        if (launchSettingCollectionIdentifiers.includes(launchSettingIdentifier)) {
          return false;
        }
        launchSettingCollectionIdentifiers.push(launchSettingIdentifier);
        return true;
      });
      return [...launchSettingsToAdd, ...launchSettingCollection];
    }
    return launchSettingCollection;
  }

  protected convertValueFromClient<T extends ILaunchSetting | NewLaunchSetting | PartialUpdateLaunchSetting>(launchSetting: T): RestOf<T> {
    return {
      ...launchSetting,
      launchAt: launchSetting.launchAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestLaunchSetting): ILaunchSetting {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestLaunchSetting[]): ILaunchSetting[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
