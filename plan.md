# Abofonsa BridgeCare — Launch Preview Site

A JHipster monolith for the **Health Connect** launch on **1 February 2027**: one public page
(countdown, waitlist capture, service teaser, pledge hand-off, social + contact) plus a protected
mini admin dashboard over the captured emails and their metrics.

## Decisions

| Question          | Decision                                                           | Why                                                                                                                                                         |
| ----------------- | ------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| JHipster shape    | **Generated** with the local CLI 9.1.0 from `abofonsa-preview.jdl` | Authentic layout and naming for free; `net.jojoaddison.abofonsa.preview` matches `hc-abofonsa-web`'s `net.jojoaddison.abofonsa`                             |
| Pledge / pre-book | **Entice and hand off** to `fund.abofonsa.com`                     | That platform already owns identity, payment, vouchers and certificates end to end. This app displays the offer and counts the click — it never takes money |
| Admin auth        | **JHipster JWT + `ROLE_ADMIN`**                                    | Standard user/authority tables; seeded admin, password from env at first boot                                                                               |
| Teaser content    | **Real services and plans** from `hc-abofonsa-web`                 | Six care services, and PEAR / PAWPAW / MELON at their real GHS pricing — not the placeholder platform cards in the demo HTML                                |
| Database          | **PostgreSQL**                                                     | Matches `hc-crowdfund-app`. The metrics drill-down is `date_trunc` group-bys; `hc-abofonsa-web`'s Mongo is the wrong shape for it                           |
| Java              | **21** (`/usr/lib/jvm/java-21-openjdk-amd64`)                      | JHipster 9.1 targets 17/21; the box defaults to `JAVA_HOME=jdk-26-oracle-x64`                                                                               |

`jhipster-mcp` is **not available** in this session — the local `jhipster` CLI (9.1.0) is used instead,
which is the same generator the MCP server would have driven.

## Reference material

- `docs/design/abofonsa-countdown.html` — the design guideline (dark teal/gold, ambient orbs, glass countdown units)
- `hc-abofonsa-web/web/src/styles/{theme.scss,_theme-colors.scss,brand.css}` — brand tokens
- `hc-abofonsa-web/api/.../dbmigrations/V004SeedServices.java`, `V005SeedPlans.java` — service and plan copy
- `hc-crowdfund-app/backend/.../db/migration/V2__seed_tiers.sql`, `V8__*.sql` — pledge tiers and voucher values

Note the two palettes differ: the countdown page is dark teal/gold (`#04211f` / `#0d6e66` / `#f0b429`),
the main site is light navy/gold (`#0d3058` / `#c59437`). The preview site follows the **countdown**
treatment as instructed, and takes the _gold_ from the main site's tertiary ramp so the two read as
one brand.

---

## Phase 0 — Foundations ✅

- [x] 0.1 Read the design reference, brand tokens, service/plan seed data and pledge domain
- [x] 0.2 Write `abofonsa-preview.jdl` — 11 entities, 10 enums, 3 relationships
- [x] 0.3 Validate the JDL generates cleanly (Spring Boot 4.0.6 + Angular 21.2.14)
- [x] 0.4 Write this plan

Two JDL gotchas found and fixed during validation, both because JHipster copies JDL text verbatim
into the generated i18n JSON: a `\S` in a `pattern()` regex, and double quotes inside javadoc
comments. Both make the translation files unparseable and abort generation.

## Phase 1 — Scaffold ✅

- [x] 1.1 Move `abofonsa-countdown.html` to `docs/design/`, keep `preview-web-prompt.txt` at root
- [x] 1.2 Generate the monolith from the JDL at the repo root
- [x] 1.3 Pin the toolchain to Java 21 and Node 24; confirm `./mvnw compile` and `webapp:build:dev`
- [x] 1.4 Bring up dev Postgres via the generated compose file; confirm the app boots
- [x] 1.5 JHipster's own initial commit stands; subsequent commits left to the maintainer

### Environment pins — this box needs all three

Set by `.java-version` and `.nvmrc`, and they are **not** optional:

- **Java 21.** `JAVA_HOME` defaults to `jdk-26-oracle-x64`, which JHipster's maven-enforcer rejects
  outright: _"JHipster supports JDK 21 to 25."_ Export
  `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` before any `./mvnw` command.
- **Node 24.18.0.** The generated `package.json` declares `engines: node >= 24.16.0`. On the shell
  default of 22.22.2, `npm install` dies inside arborist with
  `Cannot read properties of null (reading 'edgesOut')` while resolving vitest's peer set — an
  unhelpful crash that looks nothing like a version problem. `nvm use` picks it up from `.nvmrc`.
- **Port 8083, not 8080.** This box already runs `abofonsa_api` on 8080, another JVM on 8081 and
  VS Code on 8082. The port is changed in the JDL as well as the generated config, so regenerating
  does not silently move it back.

`npm` warns that it blocked native postinstall scripts (esbuild, lmdb, `@parcel/watcher`). The
frontend builds regardless — the platform-specific optional packages cover it — so the warning is
noise, not a missing step.

## Phase 2 — Domain hardening and seed data ✅

- [x] 2.1 Liquibase changelog for the nine indexes the dashboard depends on
- [x] 2.2 Seed `LaunchSetting` — launch `2027-02-01T00:00:00Z`, fund URL, contact email
- [x] 2.3 Seed the six `CareServiceTeaser` rows + 24 highlights
- [x] 2.4 Seed `CarePlanTeaser` — PEAR 3 000, PAWPAW 5 000, MELON 8 000 GHS/month + 21 features
- [x] 2.5 Seed `PledgeTierTeaser` — Bronze 1 000, Silver 5 000, Gold 50 000 GHS + 7 perks + hand-off URLs
- [x] 2.6 Seed four `LaunchMilestone` rows and five `SocialLink` rows
- [x] 2.7 `AdminAccountInitializer` resets the admin password from `ABOFONSA_ADMIN_PASSWORD`

Verified against the running database: all seed counts correct, all nine indexes present, and
`POST /api/authenticate` returns 200 for the configured password and 401 for JHipster's seeded
`admin`/`admin`.

Two things worth knowing about this phase:

- **Seed ids are hand-assigned below 1050**, which is where `sequence_generator` starts. Runtime
  inserts therefore cannot collide with a seed row.
- **`ux_metric_rollup__identity` uses `NULLS NOT DISTINCT`.** `dimension_name`/`dimension_value` are
  null on a total row, and under Postgres's default NULL semantics two total rows for the same
  bucket both satisfy a plain unique constraint — precisely the duplicate the index exists to stop.
  Needs Postgres 15+; dev and prod are on 18.
- **Service and plan copy is English only.** It is carried over verbatim from `hc-abofonsa-web`,
  which stays the single source of truth for it; slugs match so the two reconcile without a mapping
  table. Page chrome is still localised in all four languages.

## Phase 3 — Public API ✅

All under `/api/public/**`, unauthenticated, rate-limited.

- [x] 3.1–3.2 `GET /api/public/content` — launch settings, countdown target, milestones, services,
      plans, tiers and social links in **one** payload. Merged the two planned endpoints: the page is
      a single scroll with no navigation, so a second round trip buys nothing.
- [x] 3.3 `POST /api/public/waitlist` — normalisation, consent, honeypot and dwell-time checks
- [x] 3.4 `GET /api/public/waitlist/confirm` — double opt-in by token, idempotent
- [x] 3.5 `GET /api/public/waitlist/unsubscribe` — by token (GET, not POST: it is a link in an email)
- [x] 3.6 `POST /api/public/events` — beacon, restricted to browser-reportable event types
- [x] 3.7 `SecurityConfig` — `/api/public/**` permitted, everything else unchanged
- [x] 3.8 `PublicApiResourceIT` — 11 tests, all passing

Verified against the running app: content 200 with correct counts and ordering; case-differing
duplicate detected; honeypot, fast-dwell, missing consent and malformed address all 400; rate limit
429 on the sixth submission in an hour; confirm/unsubscribe redirect correctly and are idempotent;
`/api/admin/**`, the entity CRUD APIs and `/api/register` all 401 anonymously.

Decisions worth recording:

- **`/api/register` is no longer permitted.** JHipster opens self-service registration by default.
  This app has exactly one privileged account, seeded and given its password from the environment,
  so a public sign-up endpoint could only ever be used against us.
- **The opt-in token is never returned in the response body.** Echoing it would let anyone who can
  post an address confirm it themselves, which is the whole of double opt-in.
- **The beacon takes an allow-list, not a deny-list.** `WAITLIST_SUBMIT`/`CONFIRM`/`DUPLICATE` are
  written server-side only — otherwise anyone could inflate the signup count without signing up,
  and that count is the number the dashboard exists to report.
- **IP and session hashes are salted and truncated**, and the session hash mixes in the UTC date so
  it rotates at midnight: `UNIQUE_VISITORS` stays a daily-distinct count rather than becoming a
  durable identifier. `ABOFONSA_HASH_SALT` must be set per environment — unsalted, the whole IPv4
  space can be hashed in minutes and the protection is illusory.
- **A failed confirmation email does not fail the capture.** With no SMTP configured the opt-in link
  is logged instead, so the flow is still completable in dev.

### `@Lob` on PostgreSQL — a generated-code fix that regeneration will undo

Every `TextBlob` field came out as a bare `@Lob String`. Hibernate maps that to a large-object
`oid` and reads it with `getLong()`, but Liquibase created the column as `text`, so _every_ read of
a service blurb, plan description, tier blurb or milestone body failed at runtime with
`Bad value for type long`. Each of the four entities now carries
`@JdbcTypeCode(SqlTypes.LONGVARCHAR)` beside its `@Lob`, with a comment saying so. **Re-running the
entity generator removes these annotations again** — re-apply them before trusting the app.

## Phase 4 — Public page ✅

- [x] 4.1 Ported the countdown palette to SCSS, taking the _gold_ from the main site's brand
      (`#c59437`) rather than the mock's `#f0b429` so the two read as one brand
- [x] 4.2 Layout shell — ambient orbs, masked grid, header, footer
- [x] 4.3 Countdown driven by `LaunchSetting.launchAt`, not a constant in the bundle
- [x] 4.4 Waitlist form — validation, honeypot, submitting/ok/error/throttled states
- [x] 4.5 Services teaser — the six real services
- [x] 4.6 Plans teaser — PEAR / PAWPAW / MELON with real GHS pricing
- [x] 4.7 Pledge hand-off — three tiers, CTA to `fund.abofonsa.com`, click counted on the way out
- [x] 4.8 Roadmap timeline, social + contact footer
- [x] 4.9 Reveal-on-scroll, `prefers-reduced-motion`, responsive
- [x] 4.10 i18n for en / fr / es / de
- [x] 4.11 SEO and Open Graph meta
- [x] 4.12 Confirm and unsubscribe landing pages
- [x] Removed the login page's "Register a new account" and "Forgot your password?" links — the
      endpoints behind them are no longer permitted, so they could only end in a 401

Verified in a real browser: the page renders the design, the countdown ticks from the seeded
launch instant, a signup submitted through the form lands in the database and the emailed link
confirms it, `/login` still renders the untouched JHipster chrome, and the console is clean.
`npm run lint` and the production build both pass.

### Three bugs worth remembering, all found by looking at the rendered page

1. **Child components were unstyled.** `.countdown` and the waitlist form live in child components,
   and Angular's emulated encapsulation never applies a parent's styles inside a child's template —
   so the countdown rendered as four lines of plain text and the honeypot was _visible_. Fixed by
   nesting the whole stylesheet under `.abf-launch` and setting `ViewEncapsulation.None`. The
   nesting is what makes that safe: without it, `.btn` and `.card` would escape into the Bootstrap
   the admin screens depend on.
2. **Bootstrap leaks inward.** Scoping stops our rules escaping; it does nothing to stop Bootstrap's
   arriving. The service card headings inherited `.card { color: var(--bs-body-color) }` and were
   rendered near-invisible on the dark background. Colour is now asserted, never assumed, on every
   container whose class name Bootstrap also claims.
3. **Every section below the hero was permanently invisible.** The reveal-on-scroll ran in
   `ngAfterViewInit`, but the sections sit behind an `@if` on the loaded content — at that moment
   the request was still in flight, the query was empty, nothing was observed, and `.reveal` starts
   at `opacity: 0`. Now driven by an `effect()` on the `viewChildren` signal, which re-runs when the
   elements actually appear.

## Phase 5 — Metrics engine ✅

- [x] 5.1 `MetricRollupEngine` — recomputes every metric at every granularity from the raw log
      (named "Engine" because JHipster already generated a `MetricRollupService` for the entity)
- [x] 5.2 `MetricRollupScheduler` — 10-minute incremental over the last two hours, nightly full rebuild
- [x] 5.3 `GET /api/admin/metrics/series?metric=&bucket=&from=&to=&dimension=` — drill-down and zoom
- [x] 5.4 `GET /api/admin/metrics/summary` — lifetime totals and 7-day-over-7-day deltas
- [x] 5.5 Backfill on startup, plus `POST /api/admin/metrics/rebuild` to force one
- [x] 5.6 `CSV` export of emails and of any series, with a `DataExportLog` row per export
- [x] 5.7 `MetricRollupEngineIT` — 8 tests, all passing

Verified against 416 real and synthetic events: the DAY series total matches raw SQL exactly (246),
and DAY/WEEK/MONTH/QUARTER/YEAR all agree on that total while disagreeing only on resolution. The
UTM facet split partitions it exactly (66 + 60 + 60 + 60). Exports carry a UTF-8 BOM, a
`Content-Disposition`, and an audit row naming who pulled what.

### The timezone bug, which is the one to remember

`occurred_at` is a `timestamp without time zone` holding UTC. The first version read the
`date_trunc` result as a JDBC `Timestamp`, and the driver — seeing no zone on the column —
interpreted that wall-clock in the JVM's default zone. Every bucket landed **two hours late** on
this CEST machine: `2026-07-18 00:00` was stored as `02:00`, so the chart's own lookups missed and
every series returned a total of **zero** while the underlying counts were perfectly correct.

The fix is not a smarter conversion. The aggregates now return
`extract(epoch from date_trunc(...))` — a number, which has no zone to misinterpret. Postgres
defines that on a zone-less timestamp as seconds since the epoch treating the value as UTC, which
is exactly what the column holds.

`MetricRollupEngineIT.bucketsLandOnUtcBoundariesRegardlessOfTheServerTimeZone` asserts an exact
instant rather than a count, because a count-based test passes on a UTC machine and hides this
entirely.

### Truncation is reported, never silent

An HOUR series over seven months exceeded the point cap and quietly returned a partial total that
looked complete. The window is now clamped to the most recent `MAX_POINTS` buckets **before**
querying, and the response carries `truncated: true` with the `from` actually used.

## Phase 6 — Mini admin dashboard ✅

- [x] 6.1 `ROLE_ADMIN` route guard and login (JHipster's, unchanged)
- [x] 6.2 KPI tiles — lifetime totals plus each metric's 7-day movement
- [x] 6.3 Line chart with HOUR → YEAR zoom and an optional campaign-source split
- [x] 6.4 Email table — paginated, filterable, with status badges
- [x] 6.5 Filters ride JHipster's generated criteria API (`status.equals`) — the reason the JDL
      declares `filter` on this entity
- [x] 6.6 CSV export for both the emails and the chart's own series
- [x] 6.7 A `DataExportLog` row per export
- [x] 6.8 Chart per the `dataviz` skill

Verified in the browser: tiles, chart, facet split, table and both exports. The export click was
confirmed end to end — HTTP 200, an audit row naming `admin` and 31 rows, and the file on disk.

### The chart

No charting library — the dependency would be bigger than the component, and a line over time is a
path command and a linear scale. Built to the skill's procedure: form chosen first, colours last and
**validated** rather than eyeballed (`validate_palette.js` on the five categorical slots against the
light surface — lightness band, chroma floor, CVD separation ΔE 9.1 worst adjacent, normal-vision
floor 19.6 all pass). Three slots fall under 3:1 contrast, which _obliges_ visible labels — hence the
legend, the direct labels and the table, none of which are decorative.

Also: one axis, hues in fixed order and never cycled (a filter cannot repaint the survivors), gaps
filled with explicit zeroes, and the truncation warning surfaced in the chart itself.

Two things only looking at it could catch: the direct labels **overprinted** where two series ended
on the same value ("(none)" and "newsletter" rendered as "(nond)etter"), now spread apart; and a
fixed pixel height **letterboxed** the SVG, fixed by giving the viewBox a 4:1 ratio instead.

Dark mode was removed rather than kept. The admin shell is stock Bootstrap with no dark theme, so
`prefers-color-scheme` made the chart the only element that knew about the setting — a dark chart in
a white page. Dark mode is a decision the application makes, not a component.

### Two generator/framework bugs found here

1. **Spring binds request headers into `@ModelAttribute` objects.** JHipster's criteria classes are
   model attributes, and `WaitlistSignupCriteria.userAgent` is a `StringFilter` — so the browser's
   own `User-Agent` header was bound into it, failed to convert, and **every** call to
   `GET /api/waitlist-signups` returned 400. curl with no headers worked, which is a good way to
   lose an afternoon. `CriteriaBinderAdvice` disables header binding globally; `referrer`, `host`
   and `from` are all plausible column names, so this was never specific to one field.
   Note its import: in Spring 7 `ExtendedServletRequestDataBinder` moved to `web.servlet.support`
   and the old name is a deprecated _subclass_ — an `instanceof` against the old name silently
   matches nothing, which is exactly what happened on the first attempt.
2. **JHipster 9.1.0's login page is broken out of the box.** `login.ts` declares
   `viewChild.required<ElementRef>('username')` and dereferences it in `ngAfterViewInit`, but the
   generated template never defines `#username` — so every render threw `NG0951` and the rest of
   `ngAfterViewInit` never ran. Added the missing template reference.

## Phase 7 — Hardening and delivery ✅

- [x] 7.1 Rate limiting and bot defence (done in Phase 3, verified again here)
- [x] 7.2 Unsubscribe by token; hashes salted and rotating; referrer host only
- [x] 7.3 `./mvnw verify` green — **567 tests, 0 failures, 14 skipped**
- [x] 7.4 `npm test` (142 files), `npm run lint`, `npm run webapp:build:prod` all green
- [x] 7.5 Docker compose for dev Postgres; JHipster's generated `app.yml`/jib config retained
- [x] 7.6 `README.md` and `CLAUDE.md` written; JHipster's generated README kept at
      `docs/JHIPSTER-README.md`
- [ ] 7.7 **Cypress e2e not written** — see "Not done" below
- [ ] 7.8 Commit — left to the maintainer

### Getting the suite green

The first full `./mvnw verify` was red in three separate ways, none of them flaky:

- **modernizer** rejected `Optional.isPresent()/get()`. Rewritten as `map(...)`.
- **16 `AccountResourceIT` failures** on `/api/register`, `/api/activate` and password reset — all
  endpoints this application deliberately does not permit. `@Disabled` with the reason rather than
  deleted: re-opening self-service accounts means re-enabling these tests, not rewriting them.
- **14 failures** where the content seed occupied every value of a three-member unique enum, so the
  generated CRUD tests could not insert. Fixed by `contextFilter="!test"` on the seeds, which is the
  right separation anyway — verified afterwards by **dropping and recreating the dev database** and
  confirming all nine seed tables repopulate.

Frontend was red too, and one of those was worth the check: `main.spec.ts` failed after my layout
change, so I reverted to the untouched generated files and **it failed identically** — a
generator/vitest issue, not a regression. The other two were `code: undefined` in generated test
samples (see CLAUDE.md).

The production build then failed a 4 kB per-component style budget. Moving the launch stylesheet to
a global in `angular.json` fixed the budget, removed a duplicate injection across two components,
and let `ViewEncapsulation.None` go entirely — the workaround it existed for stops being necessary
once the styles are global.

### Not done, and why

- **Cypress e2e.** The flows it would cover — signup → emailed link → confirm → row in the admin
  table, and the export round trip — were each driven end to end in a real browser and verified
  against the database instead. Worth adding before this is deployed anywhere it must stay working
  unattended.
- **Production Docker/deploy.** JHipster's `src/main/docker/app.yml` and jib config are generated and
  untouched; there is no equivalent of the siblings' `deploy.sh`. Publishing needs one, plus real
  values for `ABOFONSA_HASH_SALT`, `ABOFONSA_ADMIN_PASSWORD`, SMTP and `ABOFONSA_PUBLIC_BASE_URL`.
- **`CONTRIBUTING.md`.** `CLAUDE.md` carries the commands and the traps; a separate contributor guide
  would mostly repeat it.

## Still open

- **Email delivery.** No SMTP is configured, so confirmation links are logged rather than sent. The
  flow completes either way.
- **Pledge hand-off URLs** assume `https://fund.abofonsa.com/pledge?tier=<CODE>`. Confirm against the
  live routes.
- **Social handles.** Seeded inactive with placeholder URLs, because the design had `href="#"`. Set
  the real URLs and flip `active` — a social icon that goes nowhere is worse than no icon.
- **`ABOFONSA_HASH_SALT` must be set** in every deployed environment. The default is a visible
  placeholder, and unsalted hashes of the IPv4 space are reversible in minutes.

---

## Open items

- **Email delivery.** Double opt-in needs an SMTP sender. Until one is configured the confirmation
  link is logged rather than sent, and the flow still works end to end in dev.
- **Pledge tier hand-off URLs.** Deep-link shape into `fund.abofonsa.com` is assumed to be
  `https://fund.abofonsa.com/pledge?tier=<CODE>`; to be confirmed against the live routes.
- **Social handles.** The demo HTML has `href="#"` placeholders; real X / LinkedIn / Instagram URLs
  are seeded as blanks and toggled off until supplied.

---

## Phase 8 — Brand ✅

Triggered by `brand.png` arriving: a navy-and-gold circular badge, sampled at **`#0d3058`** and
**`#c59437`** — exactly the two values `hc-abofonsa-web` already generates its Material theme from.

- [x] 8.1 The badge replaces the generated `+` glyph in the launch page header and footer, the
      JHipster mascot in the admin navbar, `favicon.ico`, and the four PWA icons
- [x] 8.2 Re-themed the launch page from the mock's teal to the brand's navy, keeping the mock's
      structure. Every teal token, literal and `rgb()` channel triple was replaced
- [x] 8.3 `og:image` / `twitter:image` added (absolute — a crawler resolves them against nothing)
- [x] 8.4 Manifest name, `theme_color` and `background_color` aligned to the brand
- [x] 8.5 `global.title` is "Health Connect", not "AbofonsaPreview"
- [x] 8.6 Deleted `app/home/` — unreachable since Phase 4 pointed `''` at the launch page, and it
      rendered a JHipster mascot — plus the now-orphaned mascot assets

Verified in the browser: header, services, plans, pledge, roadmap and footer all render in navy/gold
with no leftover teal and no console errors; every brand asset returns 200. Lint, 141 frontend test
files, the production build and `./mvnw compile` are all green.

### Two things the retheme quietly broke, and one it exposed

- **The gold gradients went flat.** `--gold-dp` folded into `--gold`, so
  `linear-gradient(140deg, var(--gold), var(--gold-dp))` became a two-stop gradient between one
  colour and itself. Restored with a lighter `--gold-lt`.
- **`.mark` was doing two unrelated jobs** — the logo tile _and_ the ✓/— glyph in the plan feature
  list. It only rendered correctly because a more specific selector happened to win. The logo is now
  `.brand-mark`.
- **The pre-boot loading screen was JHipster's**: a pacman eating JHipster logos, and — after four
  seconds — a troubleshooting panel telling the visitor to run `npm install`. That panel would have
  been shown to real visitors, on a page whose stated audience is largely mid-range Android over a
  slow connection, where four seconds is an ordinary load rather than a fault. Both replaced: a
  pulsing badge on the launch page's navy, and a message that is true for a visitor.

---

## Phase 9 — Ownership ✅

Health Connect · Abofonsa BridgeCare is a product of **Jojo Addison Consultancy**
(jojoaddison.net), and the page now says so.

- [x] 9.1 `LaunchSetting.parentCompanyName` / `parentCompanyUrl` — content, so it lives in a row
      like the launch date and the fund URL, not hard-coded in a template
- [x] 9.2 A **new** changelog (`20260730220000`), not an edit to the applied seed: add nullable →
      backfill → `NOT NULL`, because adding a NOT NULL column outright fails on any database that
      already holds the settings row
- [x] 9.3 Threaded through `PublicContentDTO.Launch`, `PublicContentService` and `launch.model.ts`
- [x] 9.4 **Top** — a full-width strip above the header: "A product of Jojo Addison Consultancy ·
      jojoaddison.net". Above the header rather than in it, so it reads as a statement about the
      site rather than another nav item competing with the pledge CTA
- [x] 9.5 **Bottom** — the full sentence in the footer, plus the link
- [x] 9.6 Copyright is now **© Jojo Addison Consultancy**, not Abofonsa BridgeCare — the consultancy
      holds it, not the brand the product is sold under
- [x] 9.7 The admin shell's footer carries the same statement, replacing generator placeholder text
      that read "This is your footer"
- [x] 9.8 `owner.prefix` / `owner.statement` in en / fr / es / de; fixtures updated for the two new
      NOT NULL columns

`567` backend tests, `141` frontend test files, lint and the production build all green; the
migration was verified applying to the existing dev database rather than only to a fresh one.

**One trap worth remembering:** in JHipster's `global.json`, `footer` is a **top-level** key, not a
member of `global`. Adding it under `global` compiles, builds and passes every test — and renders
`translation-not-found[footer.productOf]` on screen. Only looking at the page catches it.
