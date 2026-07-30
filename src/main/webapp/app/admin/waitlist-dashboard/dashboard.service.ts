import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { BucketType, MetricKey, MetricSeries, MetricSummary, WaitlistSignupRow } from './dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  private readonly metricsUrl = this.applicationConfigService.getEndpointFor('api/admin/metrics');
  private readonly exportUrl = this.applicationConfigService.getEndpointFor('api/admin/export');
  private readonly signupsUrl = this.applicationConfigService.getEndpointFor('api/waitlist-signups');

  summary(): Observable<MetricSummary> {
    return this.http.get<MetricSummary>(`${this.metricsUrl}/summary`);
  }

  series(metric: MetricKey, bucket: BucketType, dimension?: string | null): Observable<MetricSeries> {
    let params = new HttpParams().set('metric', metric).set('bucket', bucket);
    if (dimension) {
      params = params.set('dimension', dimension);
    }
    return this.http.get<MetricSeries>(`${this.metricsUrl}/series`, { params });
  }

  /**
   * The captured emails, through JHipster's generated criteria API.
   *
   * <p>Reusing the generated endpoint rather than writing another one is the whole reason the JDL
   * declares `filter` on this entity: `status.equals`, `capturedAt.greaterThanOrEqual` and the rest
   * come for free, and the table's filters map straight onto them.
   */
  signups(params: Record<string, string | number | boolean>): Observable<HttpResponse<WaitlistSignupRow[]>> {
    let httpParams = new HttpParams();
    Object.entries(params).forEach(([key, value]) => {
      // Only the empty string is reachable — the parameter type already excludes null/undefined.
      if (value !== '') {
        httpParams = httpParams.set(key, String(value));
      }
    });
    return this.http.get<WaitlistSignupRow[]>(this.signupsUrl, { params: httpParams, observe: 'response' });
  }

  waitlistCsvUrl(status?: string | null): string {
    const query = status ? `?status=${encodeURIComponent(status)}` : '';
    return `${this.exportUrl}/waitlist.csv${query}`;
  }

  metricsCsvUrl(metric: MetricKey, bucket: BucketType, dimension?: string | null): string {
    const query = new URLSearchParams({ metric, bucket });
    if (dimension) {
      query.set('dimension', dimension);
    }
    return `${this.exportUrl}/metrics.csv?${query.toString()}`;
  }

  /**
   * Downloads a CSV through XHR rather than pointing the browser at the URL.
   *
   * <p>A plain link cannot carry the bearer token — the export endpoints require ROLE_ADMIN, and a
   * naked navigation arrives unauthenticated and 401s. Fetching the blob and clicking a synthetic
   * anchor keeps the interceptor in the path.
   */
  download(url: string, filename: string): Observable<Blob> {
    return this.http.get(url, { responseType: 'blob' });
  }
}

/**
 * Saves a fetched blob to disk.
 *
 * A module function rather than a static member: the lint rule wants every field declared before
 * any static method, which would push the injections below it for no benefit. This never touched
 * instance state anyway.
 */
export function saveBlob(blob: Blob, filename: string, doc: Document): void {
  const objectUrl = URL.createObjectURL(blob);
  const anchor = doc.createElement('a');
  anchor.href = objectUrl;
  anchor.download = filename;
  doc.body.appendChild(anchor);
  anchor.click();
  doc.body.removeChild(anchor);
  // Revoking immediately can cancel the download in some browsers; a tick is enough.
  setTimeout(() => URL.revokeObjectURL(objectUrl), 1000);
}
