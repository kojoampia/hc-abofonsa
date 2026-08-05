import { HttpClient, httpResource } from '@angular/common/http';
import { DOCUMENT, Injectable, computed, inject } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { CaptureEventRequest, CaptureEventType, LaunchContent, OptInResult, WaitlistReceipt, WaitlistSubmission } from './launch.model';

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
   * Complete double opt-in.
   *
   * POST, from a button on the landing page, rather than the emailed link doing the work itself.
   * Both of these were GETs answered straight from the URL in the message, and mail clients and
   * security gateways prefetch links — so a scanner could confirm a subscription the recipient
   * never agreed to, which turns the consent record into a record of what a robot did.
   */
  confirmWaitlist(token: string): Observable<OptInResult> {
    return this.http.post<OptInResult>(`${this.waitlistUrl}/confirm`, { token });
  }

  unsubscribeWaitlist(token: string): Observable<OptInResult> {
    return this.http.post<OptInResult>(`${this.waitlistUrl}/unsubscribe`, { token });
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
