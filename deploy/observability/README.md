# Observability

This application reports through the shared OpenTelemetry pipeline on `webserver`. Nothing here is
scraped: Alloy's config on that host states _"NO APPLICATION SCRAPE TARGETS, and that is correct for
this host"_, because applications push OTLP to a collector instead.

```
backend  ──OTLP/gRPC──►  otel-collector:4317  ──►  Tempo   (traces)
                                              ──►  Mimir   (metrics)
                                              ──►  Loki    (logs)

browser  ──POST /v1/traces──►  nginx (rate limited)  ──►  otel-collector:4319  ──►  Tempo only
```

`/management/prometheus` remains enabled and is **not** the path anything uses. It costs nothing and
is what you have if the collector is down.

## Where to look

| Question                                | Where                                                                       |
| --------------------------------------- | --------------------------------------------------------------------------- |
| Is the service up, how fast, how busy?  | Grafana → Technical → **Service Deep Dive**, service `abofonsa-preview`      |
| How does it compare to the other apps?  | Grafana → Technical → **Apps RED Overview**                                  |
| Is the signup funnel working?           | **Waitlist Funnel** (this directory — see below)                            |
| What happened in one request?           | Tempo, `service.name = abofonsa-preview`                                    |
| What did a visitor's browser experience? | Tempo, `service.name = abofonsa-preview-web` (spans tagged `telemetry.source=browser`) |
| What did the log say?                   | Loki, `{service_name="abofonsa-preview"}`                                   |

The service appears in Service Deep Dive automatically — that dashboard templates on
`label_values(jvm_thread_count, service_name)`, and the agent emits `jvm_*`.

## Installing the Waitlist Funnel dashboard

`waitlist-funnel-dashboard.json` is not installed by `deploy.sh`. The monitoring stack lives in a
different, root-owned repository (`/root/webroot/02-monitoring`) that is shared by every application
on the host, and its `grafana-dashboards.yaml` sets `allowUiUpdates: false` — dashboards are files,
provisioned from `configs/dashboards/`, and edits made in the UI are discarded. Changing another
team's repository from this one's deploy script would be the wrong direction of coupling.

To install:

```bash
scp deploy/observability/waitlist-funnel-dashboard.json webserver:/tmp/
ssh webserver 'sudo install -m 0644 /tmp/waitlist-funnel-dashboard.json \
    /root/webroot/02-monitoring/services/configs/dashboards/technical/abofonsa-preview-funnel.json \
  && docker restart grafana'
```

It belongs in `technical/` rather than `management/`: the panel descriptions name metrics and assume
the reader can read a PromQL legend, which is the documented dividing line between those two folders.

## Metric names

Micrometer meters reach Mimir through the agent's Micrometer bridge and Prometheus remote write,
which rewrites the name as `name` + `baseUnit` + `_total`. The dashboard's queries use the names as
they actually appear, read back out of Mimir rather than predicted:

| Meter                                          | In Mimir                                                 |
| ---------------------------------------------- | -------------------------------------------------------- |
| `abofonsa.waitlist.signups`                     | `abofonsa_waitlist_signups_submissions_total`             |
| `abofonsa.waitlist.opt_in`                      | `abofonsa_waitlist_opt_in_events_total`                   |
| `security.authentication.failures`              | `security_authentication_failures_attempts_total`         |
| `abofonsa.waitlist.confirmation_mail_failures`  | `abofonsa_waitlist_confirmation_mail_failures_messages_total` |

The first three were confirmed live. The fourth follows the same rule but had not fired at the time
of writing — exercising it means sending a real confirmation email and having it fail.

**A meter appears in Mimir only after it is first incremented.** A counter registered at zero is not
exported, so a panel for something that has never happened shows "No data" rather than a flat zero.

There is a corollary that will waste your time otherwise: because the series does not exist until the
first increment, its **first stored sample is already 1**. Until the counter increments again,
`increase()` over a range will be 0 (or absent if there are not yet two samples), so a panel stays
empty until the *second* occurrence. Confirmed while verifying this dashboard — one honeypot
submission produced `increase(...[10m]) = 0`, a second produced `1.23` (extrapolated, as
`increase()` does). Nothing was wrong with the query either time.

## Two things that will silently produce nothing

- **`OTEL_INSTRUMENTATION_MICROMETER_ENABLED` is off by default in the agent.** Without it the JVM
  and HTTP metrics still arrive and every dashboard looks healthy, but the application's own counters
  are simply absent. This was found by querying Mimir for a counter and not finding it, having
  already deployed believing it worked.
- **The browser SDK is loaded only if the visit is sampled**, at `RUM_SAMPLE_RATIO` (default 0.25).
  Three quarters of page loads legitimately produce no spans at all. When testing, set it to `1.0`
  in `.env` and restart, or you will be debugging a sampler.
