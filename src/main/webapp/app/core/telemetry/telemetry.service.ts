import { DOCUMENT, Injectable, inject } from '@angular/core';

import { TelemetryConfig } from 'app/launch/launch.model';

/**
 * Real User Monitoring for the launch page, via OpenTelemetry.
 *
 * <p>Browser spans are posted same-origin to `/v1/traces`, which nginx proxies to the collector's
 * separate browser receiver. That receiver exports to Tempo and nothing else, so a flood of forged
 * spans costs trace storage and cannot touch Mimir's series count or Loki's log volume. The scrubbing
 * of query strings happens there too, server-side, where a caller cannot skip it.
 *
 * <p>Three deliberate properties, each of which took a decision:
 *
 * <ul>
 *   <li><b>Same-origin endpoint.</b> The Content-Security-Policy is `default-src 'self'` with no
 *       `connect-src` exception. Posting to a third-party RUM host would mean widening that, which
 *       is a poor trade for telemetry — so the collector is reached through our own nginx instead.
 *   <li><b>Sampled before the SDK is loaded.</b> The decision is made first and the OpenTelemetry
 *       modules are `import()`ed only if this visit is sampled, so an unsampled visitor never
 *       downloads ~90 kB of instrumentation. This audience is largely on mid-range Android over
 *       mobile data; making them pay for observability they are not part of would be rude.
 *   <li><b>Per visit, not per request.</b> Half a trace is worse than none when the question is
 *       where a signup stalled, so a page load is either traced end to end or not at all.
 * </ul>
 *
 * <p>No user identifier is set, here or anywhere. The rest of this application goes to some length
 * to avoid holding a durable identifier for a visitor — salted hashes that rotate at midnight, a
 * referrer reduced to its host — and RUM is not an excuse to reintroduce one by the back door.
 */
@Injectable({ providedIn: 'root' })
export class TelemetryService {
  private started = false;

  private readonly document = inject(DOCUMENT);

  /**
   * Starts RUM if this visit is sampled. Safe to call more than once; the second call is a no-op.
   *
   * <p>Config comes from the server (inside `/api/public/content`) rather than the bundle, so the
   * sampling rate is an environment variable and a restart rather than a rebuild and a redeploy.
   */
  start(config: TelemetryConfig | undefined): void {
    if (this.started || !config?.enabled) {
      return;
    }
    // Guard the SDK against environments it cannot work in — server-side rendering, and the
    // prerender pass of a production build, both of which have no window.
    const win = this.document.defaultView;
    if (!win?.performance) {
      return;
    }
    if (Math.random() >= config.sampleRatio) {
      // Not sampled: mark as started so a later call does not roll the dice again mid-visit and
      // produce a trace with no document-load span at its root.
      this.started = true;
      return;
    }
    this.started = true;

    void this.bootstrap(config).catch(() => {
      // Telemetry must never be the reason a page fails. A failed dynamic import — an ad blocker,
      // a stale chunk after a deploy, an offline visitor — is silently the end of it.
    });
  }

  private async bootstrap(config: TelemetryConfig): Promise<void> {
    const [
      { WebTracerProvider, BatchSpanProcessor },
      { OTLPTraceExporter },
      { registerInstrumentations },
      { DocumentLoadInstrumentation },
      { FetchInstrumentation },
      { XMLHttpRequestInstrumentation },
      { resourceFromAttributes },
    ] = await Promise.all([
      import('@opentelemetry/sdk-trace-web'),
      import('@opentelemetry/exporter-trace-otlp-http'),
      import('@opentelemetry/instrumentation'),
      import('@opentelemetry/instrumentation-document-load'),
      import('@opentelemetry/instrumentation-fetch'),
      import('@opentelemetry/instrumentation-xml-http-request'),
      import('@opentelemetry/resources'),
    ]);

    const exporter = new OTLPTraceExporter({ url: config.endpoint });

    const provider = new WebTracerProvider({
      resource: resourceFromAttributes({
        // Distinct from the backend's `abofonsa-preview`, so a browser span and the server span it
        // caused are separable in Tempo even though they share a trace. The collector additionally
        // stamps telemetry.source=browser, which cannot be spoofed by a caller.
        'service.name': 'abofonsa-preview-web',
        'service.version': this.appVersion(),
      }),
      spanProcessors: [
        new BatchSpanProcessor(exporter, {
          // Small and frequent. A launch page visit is short, and a batch still sitting in memory
          // when the tab closes is a batch nobody ever sees.
          maxQueueSize: 128,
          maxExportBatchSize: 32,
          scheduledDelayMillis: 2000,
        }),
      ],
    });

    // No ZoneContextManager: this application is zoneless (there is no zone.js dependency at all),
    // so the default context manager is the correct one. Registering the Zone-based manager without
    // zone.js loaded throws during startup.
    provider.register();

    // Never trace the telemetry endpoint itself. Without this the exporter's own POST is
    // instrumented, which produces a span, which is exported, which produces a span.
    const ignoreUrls = [/\/v1\/traces$/];

    registerInstrumentations({
      instrumentations: [
        new DocumentLoadInstrumentation(),
        // Angular's HttpClient uses XHR here — `provideHttpClient` is configured without
        // `withFetch()` — so the XHR instrumentation is the one that matters for API calls. Fetch is
        // registered too because `httpResource` and any future `withFetch()` would use it.
        new XMLHttpRequestInstrumentation({ ignoreUrls, propagateTraceHeaderCorsUrls: [] }),
        new FetchInstrumentation({ ignoreUrls, propagateTraceHeaderCorsUrls: [] }),
      ],
    });
  }

  /** Best-effort build identity, read from the meta tag Angular's index.html carries. */
  private appVersion(): string {
    return this.document.querySelector('meta[name="version"]')?.getAttribute('content') ?? 'unknown';
  }
}
