import { HttpClient, httpResource } from '@angular/common/http';
import { DOCUMENT, Injectable, computed, inject } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { CaptureEventRequest, CaptureEventType, LaunchContent, WaitlistReceipt, WaitlistSubmission } from './launch.model';

@Injectable({ providedIn: 'root' })
export class LaunchService {
  readonly contentResource = httpResource<LaunchContent>(() => this.contentUrl);
  readonly content = computed(() => (this.contentResource.hasValue() ? this.contentResource.value() : undefined));
  readonly loading = computed(() => this.contentResource.isLoading());
  readonly failed = computed(() => this.contentResource.error() !== undefined);

  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);
  private readonly document = inject(DOCUMENT);

  private readonly contentUrl = this.applicationConfigService.getEndpointFor('api/public/content');
  private readonly waitlistUrl = this.applicationConfigService.getEndpointFor('api/public/waitlist');
  private readonly eventsUrl = this.applicationConfigService.getEndpointFor('api/public/events');

  submitWaitlist(submission: WaitlistSubmission): Observable<WaitlistReceipt> {
    return this.http.post<WaitlistReceipt>(this.waitlistUrl, submission);
  }

  /**
   * Fire-and-forget analytics.
   *
   * <p>Uses `navigator.sendBeacon` where available, which is the only way a click that navigates
   * away from the page reliably reports itself — an in-flight XHR is cancelled when the document
   * unloads, and the pledge hand-off is exactly that kind of click. Falls back to a POST, and
   * swallows every failure: a missing analytics row must never break the page.
   */
  recordEvent(eventType: CaptureEventType, targetKey?: string): void {
    const body: CaptureEventRequest = {
      eventType,
      locale: this.document.documentElement.lang || 'en',
      sourcePage: this.document.location.pathname,
      targetKey: targetKey ?? null,
      ...this.campaignParams(),
    };

    const nav = this.document.defaultView?.navigator;
    if (nav?.sendBeacon) {
      const blob = new Blob([JSON.stringify(body)], { type: 'application/json' });
      if (nav.sendBeacon(this.eventsUrl, blob)) {
        return;
      }
    }
    this.http.post(this.eventsUrl, body).subscribe({ error: () => undefined });
  }

  private campaignParams(): Pick<CaptureEventRequest, 'utmSource' | 'utmMedium' | 'utmCampaign'> {
    const params = new URLSearchParams(this.document.location.search);
    return {
      utmSource: params.get('utm_source'),
      utmMedium: params.get('utm_medium'),
      utmCampaign: params.get('utm_campaign'),
    };
  }
}
