# Abofonsa BridgeCare — Launch Preview Site

A JHipster monolith for the **Health Connect** launch on **1 February 2027**: one public page
(countdown, waitlist capture, service teaser, pledge hand-off, social + contact) plus a protected
mini admin dashboard over the captured emails and their metrics.

## Decisions

| Question | Decision | Why |
| --- | --- | --- |
| JHipster shape | **Generated** with the local CLI 9.1.0 from `abofonsa-preview.jdl` | Authentic layout and naming for free; `net.jojoaddison.abofonsa.preview` matches `hc-abofonsa-web`'s `net.jojoaddison.abofonsa` |
| Pledge / pre-book | **Entice and hand off** to `fund.abofonsa.com` | That platform already owns identity, payment, vouchers and certificates end to end. This app displays the offer and counts the click — it never takes money |
| Admin auth | **JHipster JWT + `ROLE_ADMIN`** | Standard user/authority tables; seeded admin, password from env at first boot |
| Teaser content | **Real services and plans** from `hc-abofonsa-web` | Six care services, and PEAR / PAWPAW / MELON at their real GHS pricing — not the placeholder platform cards in the demo HTML |
| Database | **PostgreSQL** | Matches `hc-crowdfund-app`. The metrics drill-down is `date_trunc` group-bys; `hc-abofonsa-web`'s Mongo is the wrong shape for it |
| Java | **21** (`/usr/lib/jvm/java-21-openjdk-amd64`) | JHipster 9.1 targets 17/21; the box defaults to `JAVA_HOME=jdk-26-oracle-x64` |

`jhipster-mcp` is **not available** in this session — the local `jhipster` CLI (9.1.0) is used instead,
which is the same generator the MCP server would have driven.

## Reference material

- `docs/design/abofonsa-countdown.html` — the design guideline (dark teal/gold, ambient orbs, glass countdown units)
- `hc-abofonsa-web/web/src/styles/{theme.scss,_theme-colors.scss,brand.css}` — brand tokens
- `hc-abofonsa-web/api/.../dbmigrations/V004SeedServices.java`, `V005SeedPlans.java` — service and plan copy
- `hc-crowdfund-app/backend/.../db/migration/V2__seed_tiers.sql`, `V8__*.sql` — pledge tiers and voucher values

Note the two palettes differ: the countdown page is dark teal/gold (`#04211f` / `#0d6e66` / `#f0b429`),
the main site is light navy/gold (`#0d3058` / `#c59437`). The preview site follows the **countdown**
treatment as instructed, and takes the *gold* from the main site's tertiary ramp so the two read as
one brand.

---

## Phase 0 — Foundations ✅

- [x] 0.1 Read the design reference, brand tokens, service/plan seed data and pledge domain
- [x] 0.2 Write `abofonsa-preview.jdl` — 11 entities, 10 enums, 3 relationships
- [x] 0.3 Validate the JDL generates cleanly (Spring Boot 4.0.6 + Angular 21.2.14)
- [ ] 0.4 Write this plan

Two JDL gotchas found and fixed during validation, both because JHipster copies JDL text verbatim
into the generated i18n JSON: a `\S` in a `pattern()` regex, and double quotes inside javadoc
comments. Both make the translation files unparseable and abort generation.

## Phase 1 — Scaffold

- [ ] 1.1 Move `abofonsa-countdown.html` to `docs/design/`, keep `preview-web-prompt.txt` at root
- [ ] 1.2 Generate the monolith from the JDL at the repo root
- [ ] 1.3 Pin the Maven toolchain to Java 21; confirm `./mvnw compile`
- [ ] 1.4 Bring up dev Postgres via the generated compose file; confirm the app boots
- [ ] 1.5 First commit

## Phase 2 — Domain hardening and seed data

- [ ] 2.1 Liquibase changelog for the indexes the dashboard depends on
      (`capture_event(occurred_date, event_type)`, `metric_rollup(metric_key, bucket_type, bucket_start)` unique,
      `waitlist_signup(captured_at)`, `waitlist_signup(status)`)
- [ ] 2.2 Seed `LaunchSetting` — launch `2027-02-01T00:00:00Z`, fund URL, contact email/phone
- [ ] 2.3 Seed the six `CareServiceTeaser` rows + highlights
- [ ] 2.4 Seed `CarePlanTeaser` — PEAR 3 000, PAWPAW 5 000, MELON 8 000 GHS/month + features
- [ ] 2.5 Seed `PledgeTierTeaser` — Bronze 1 000, Silver 5 000, Gold 50 000 GHS + perks + hand-off URLs
- [ ] 2.6 Seed four `LaunchMilestone` rows and the `SocialLink` set
- [ ] 2.7 Seed the `ROLE_ADMIN` user; password supplied by env, never committed

## Phase 3 — Public API

All under `/api/public/**`, unauthenticated, rate-limited.

- [ ] 3.1 `GET /api/public/launch` — settings, countdown target, milestones
- [ ] 3.2 `GET /api/public/content` — services, plans, tiers, social links; locale-resolved and cached
- [ ] 3.3 `POST /api/public/waitlist` — capture with normalisation, consent, honeypot and dwell-time checks
- [ ] 3.4 `GET /api/public/waitlist/confirm` — double opt-in by token
- [ ] 3.5 `POST /api/public/waitlist/unsubscribe` — by token
- [ ] 3.6 `POST /api/public/events` — beacon for `PAGE_VIEW`, `PLEDGE_CTA_CLICK`, `SERVICE_VIEW`, `SOCIAL_CLICK`
- [ ] 3.7 `SecurityConfig` — permit `/api/public/**`, authenticate the rest, `ROLE_ADMIN` on `/api/admin/**`
- [ ] 3.8 Controller + service tests

## Phase 4 — Public page

- [ ] 4.1 Port the countdown palette to SCSS tokens layered on the JHipster theme
- [ ] 4.2 Layout shell — ambient orbs, masked grid, header, footer
- [ ] 4.3 Countdown component driven by `LaunchSetting`, not a hard-coded constant
- [ ] 4.4 Waitlist form — validation, honeypot, submitting/ok/error states
- [ ] 4.5 Services teaser section
- [ ] 4.6 Plans teaser section (PEAR / PAWPAW / MELON)
- [ ] 4.7 Pledge hand-off section — tiers, CTA to `fund.abofonsa.com`, click tracked before redirect
- [ ] 4.8 Roadmap timeline, social + contact footer
- [ ] 4.9 Reveal-on-scroll, `prefers-reduced-motion`, responsive down to 360px
- [ ] 4.10 i18n keys for en / fr / es / de
- [ ] 4.11 SEO and Open Graph meta
- [ ] 4.12 Confirm and unsubscribe landing pages

## Phase 5 — Metrics engine

- [ ] 5.1 `MetricRollupService` — recompute rollups from `CaptureEvent` over a bucket range
- [ ] 5.2 Scheduled hourly incremental + nightly whole-day reconcile
- [ ] 5.3 `GET /api/admin/metrics/series?metric=&bucket=&from=&to=&dimension=` — the drill-down/zoom endpoint
- [ ] 5.4 `GET /api/admin/metrics/summary` — totals and period-over-period deltas
- [ ] 5.5 Backfill on startup so a fresh deploy is not blank
- [ ] 5.6 Tests, including `Africa/Accra` vs UTC bucket boundaries

## Phase 6 — Mini admin dashboard

- [ ] 6.1 `ROLE_ADMIN` route guard and login
- [ ] 6.2 KPI tiles — total, confirmed, today, last 7 days
- [ ] 6.3 Line chart of captures per day, with hour / day / week / month zoom
- [ ] 6.4 Email table — paginated, sortable, filterable
- [ ] 6.5 Drill-down filters (status, audience, UTM, date range) on JHipster criteria
- [ ] 6.6 Export — CSV / JSON / XLSX for emails and for metric series
- [ ] 6.7 Write a `DataExportLog` row per export
- [ ] 6.8 Chart styling per the `dataviz` skill, light and dark

## Phase 7 — Hardening and delivery

- [ ] 7.1 Rate limiting and bot defence on the public endpoints
- [ ] 7.2 Data-subject basics — unsubscribe, export-mine, delete-by-email
- [ ] 7.3 `./mvnw verify` green
- [ ] 7.4 Frontend test + lint + build green
- [ ] 7.5 Cypress e2e — sign up, confirm, appears in the admin table
- [ ] 7.6 Dockerfile and prod compose profile
- [ ] 7.7 `README.md`, `CLAUDE.md`, `CONTRIBUTING.md`
- [ ] 7.8 Final commit

---

## Open items

- **Email delivery.** Double opt-in needs an SMTP sender. Until one is configured the confirmation
  link is logged rather than sent, and the flow still works end to end in dev.
- **Pledge tier hand-off URLs.** Deep-link shape into `fund.abofonsa.com` is assumed to be
  `https://fund.abofonsa.com/pledge?tier=<CODE>`; to be confirmed against the live routes.
- **Social handles.** The demo HTML has `href="#"` placeholders; real X / LinkedIn / Instagram URLs
  are seeded as blanks and toggled off until supplied.
