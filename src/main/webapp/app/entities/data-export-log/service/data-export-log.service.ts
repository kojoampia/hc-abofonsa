import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IDataExportLog } from '../data-export-log.model';

type RestOf<T extends IDataExportLog> = Omit<T, 'rangeFrom' | 'rangeTo' | 'requestedAt'> & {
  rangeFrom?: string | null;
  rangeTo?: string | null;
  requestedAt?: string | null;
};

export type RestDataExportLog = RestOf<IDataExportLog>;

@Injectable()
export class DataExportLogsService {
  readonly dataExportLogsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly dataExportLogsResource = httpResource<RestDataExportLog[]>(() => {
    const params = this.dataExportLogsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of dataExportLog that have been fetched. It is updated when the dataExportLogsResource emits a new value.
   * In case of error while fetching the dataExportLogs, the signal is set to an empty array.
   */
  readonly dataExportLogs = computed(() =>
    (this.dataExportLogsResource.hasValue() ? this.dataExportLogsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/data-export-logs');

  protected convertValueFromServer(restDataExportLog: RestDataExportLog): IDataExportLog {
    return {
      ...restDataExportLog,
      rangeFrom: restDataExportLog.rangeFrom ? dayjs(restDataExportLog.rangeFrom) : undefined,
      rangeTo: restDataExportLog.rangeTo ? dayjs(restDataExportLog.rangeTo) : undefined,
      requestedAt: restDataExportLog.requestedAt ? dayjs(restDataExportLog.requestedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class DataExportLogService extends DataExportLogsService {
  protected readonly http = inject(HttpClient);

  find(id: number): Observable<IDataExportLog> {
    return this.http
      .get<RestDataExportLog>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IDataExportLog[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDataExportLog[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  getDataExportLogIdentifier(dataExportLog: Pick<IDataExportLog, 'id'>): number {
    return dataExportLog.id;
  }

  compareDataExportLog(o1: Pick<IDataExportLog, 'id'> | null, o2: Pick<IDataExportLog, 'id'> | null): boolean {
    return o1 && o2 ? this.getDataExportLogIdentifier(o1) === this.getDataExportLogIdentifier(o2) : o1 === o2;
  }

  addDataExportLogToCollectionIfMissing<Type extends Pick<IDataExportLog, 'id'>>(
    dataExportLogCollection: Type[],
    ...dataExportLogsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dataExportLogs: Type[] = dataExportLogsToCheck.filter(isPresent);
    if (dataExportLogs.length > 0) {
      const dataExportLogCollectionIdentifiers = dataExportLogCollection.map(dataExportLogItem =>
        this.getDataExportLogIdentifier(dataExportLogItem),
      );
      const dataExportLogsToAdd = dataExportLogs.filter(dataExportLogItem => {
        const dataExportLogIdentifier = this.getDataExportLogIdentifier(dataExportLogItem);
        if (dataExportLogCollectionIdentifiers.includes(dataExportLogIdentifier)) {
          return false;
        }
        dataExportLogCollectionIdentifiers.push(dataExportLogIdentifier);
        return true;
      });
      return [...dataExportLogsToAdd, ...dataExportLogCollection];
    }
    return dataExportLogCollection;
  }

  protected convertValueFromClient<T extends IDataExportLog>(dataExportLog: T): RestOf<T> {
    return {
      ...dataExportLog,
      rangeFrom: dataExportLog.rangeFrom?.toJSON() ?? null,
      rangeTo: dataExportLog.rangeTo?.toJSON() ?? null,
      requestedAt: dataExportLog.requestedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestDataExportLog): IDataExportLog {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestDataExportLog[]): IDataExportLog[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
