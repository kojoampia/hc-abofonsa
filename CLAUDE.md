# CLAUDE.md

Guidance for Claude Code (claude.ai/code) when working in this repository.

## What this is

The **Health Connect launch preview site** for Abofonsa BridgeCare — a JHipster 9.1.0 monolith
(Spring Boot 4 + Angular 21, PostgreSQL) serving one public page and one protected dashboard.

- **Public page** (`/`) — countdown to 1 February 2027, waitlist capture with double opt-in, care
  service and plan teasers, a pledge hand-off, roadmap and contact links.
- **Admin dashboard** (`/admin/waitlist-dashboard`) — the captured emails and the metrics derived
  from them, with hour→year zoom, a campaign-source split, and CSV export.

Sibling repositories, both referenced by this one:

- `hc-abofonsa-web` — the real marketing site. **Source of truth for service and plan copy.**
  Service slugs match, so the two reconcile without a mapping table.
- `hc-crowdfund-app` — the crowdfunding platform at `fund.abofonsa.com`. **Owns pledging end to
  end**: identity, payment, vouchers, certificates. This app displays tiers and counts the outbound
  click; it never takes money.

`plan.md` is the build log — phases, decisions and every trap hit along the way. `abofonsa-preview.jdl`
is the model's source of truth.

## Commands

```bash
# All three of these environment pins are required — see "Environment" below.
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
nvm use                       # reads .nvmrc → 24.18.0

docker compose -f src/main/docker/services.yml up -d     # dev Postgres on 5432
ABOFONSA_ADMIN_PASSWORD=... ./mvnw spring-boot:run       # backend on 8083
npm start                                                # Angular dev server, proxies to 8083

./mvnw verify                 # unit + integration (Testcontainers) + checkstyle + modernizer
npm test && npm run lint && npm run webapp:build:prod
```

## Environment — all three pins are load-bearing

| Pin                           | Why it is not optional                                                                                                                                                                                             |
| ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Java 21** (`.java-version`) | The box defaults to JDK 26, which JHipster's maven-enforcer rejects outright: _"JHipster supports JDK 21 to 25."_                                                                                                  |
| **Node 24.18.0** (`.nvmrc`)   | `package.json` declares `engines: node >= 24.16.0`. On Node 22, `npm install` dies inside arborist with `Cannot read properties of null (reading 'edgesOut')` — a crash that looks nothing like a version problem. |
| **Port 8083**                 | 8080 is `abofonsa_api`, 8081 another JVM, 8082 VS Code. Set in the JDL as well as the generated config, so regenerating does not move it back.                                                                     |

`npm` warns that it blocked native postinstall scripts (esbuild, lmdb, `@parcel/watcher`). The
frontend builds regardless — noise, not a missing step.

## Things that have bitten this codebase

Each of these cost real debugging time. All are commented at the site of the fix.

- **`@Lob String` on PostgreSQL reads as a large-object `oid`.** Hibernate maps a bare `@Lob` to an
  `oid` and reads it with `getLong()`, but Liquibase created the column as `text` — so _every_ read
  of a service blurb, plan description, tier blurb or milestone body failed with
  `Bad value for type long`. All four entities carry `@JdbcTypeCode(SqlTypes.LONGVARCHAR)` beside
  their `@Lob`. **Re-running the entity generator removes them again.**
- **Never read a `date_trunc` result as a JDBC `Timestamp`.** `occurred_at` is a
  `timestamp without time zone` holding UTC; the driver interprets that wall-clock in the JVM's
  default zone, so every metric bucket lands off by the server's offset. It was two hours here, and
  the visible symptom was every chart series totalling **zero** while the counts underneath were
  perfectly right. The aggregates return `extract(epoch from ...)` — a number has no zone to
  misinterpret. `MetricRollupEngineIT` asserts an exact instant, not a count, because a count-based
  test passes on a UTC machine.
- **Spring binds request _headers_ into `@ModelAttribute` objects.** JHipster's criteria classes are
  model attributes, and `WaitlistSignupCriteria.userAgent` is a `StringFilter`, so the browser's own
  `User-Agent` header was bound into it and **every** `GET /api/waitlist-signups` returned 400.
  `CriteriaBinderAdvice` disables header binding globally. Note its import: in Spring 7 the binder
  moved to `web.servlet.support` and the old name is a deprecated _subclass_, so an `instanceof`
  against the old name silently matches nothing.
- **Content seeds carry `contextFilter="!test"`.** JHipster's generated entity ITs assume they own an
  empty table, and `CarePlanTeaser.code`/`PledgeTierTeaser.code` are unique enums with three values
  each that the seed fully occupies. Tests that need content build their own fixtures.
- **Bootstrap leaks into the launch page.** Namespacing under `.abf-launch` stops our rules escaping;
  it does nothing to stop Bootstrap's arriving. Colour is asserted, never assumed, on any container
  whose class name Bootstrap also claims (`.card`, `.btn`).
- **The launch stylesheet is a global**, registered in `angular.json`. As a component style it blew
  the 4 kB budget, was injected twice, and needed `ViewEncapsulation.None` to reach the child
  components. Every selector is nested under `.abf-launch`, so this is safe.

## Two bugs in generator-jhipster 9.1.0 itself, patched here

Re-generating will reintroduce both.

1. `login.html` is missing the `#username` template reference that `login.ts` declares as
   `viewChild.required` — every render threw `NG0951`.
2. `*.test-samples.ts` emits `code: undefined` for required _unique_ enums, because the faker cannot
   produce a fourth distinct value from a three-member enum. Filled in by hand.

Also, writing a JDL: **do not put double quotes or `\` escapes in JDL comments or `pattern()`
regexes.** JHipster copies both verbatim into the generated i18n JSON, and either one makes the
translation files unparseable — generation aborts with a JSON syntax error naming no file.

## Brand

`brand.png` at the repository root is the source asset — a navy-and-gold circular badge, **`#0d3058`
and `#c59437`**, the same two values `hc-abofonsa-web` generates its Material theme from. Everything
derives from it: `content/images/abofonsa-brand.png` (corners cleared so it does not sit in a white
box on the dark page), the four PWA icons, `favicon.ico`, and the launch page's whole palette.

The launch page follows `docs/design/abofonsa-countdown.html` for _structure_ only — ambient orbs,
glass countdown cards, gradient headline. Its colour is the brand's, not the mock's teal. If you
regenerate any of the derived images, regenerate all of them from `brand.png` rather than editing
one by hand.

Two traps if you touch `content/scss/launch.scss`:

- `--gold` and `--gold-lt` are two stops of one gradient. Collapsing them to a single token turns
  every gold button and badge into a flat fill, which is easy to miss in a screenshot.
- `.brand-mark` is the logo; `.mark` is the ✓/— glyph in the plan feature list. They were the same
  class once and only rendered correctly by specificity accident.

## Ownership

Health Connect · Abofonsa BridgeCare is a product of **Jojo Addison Consultancy**
(jojoaddison.net). It is stated at the top and bottom of the launch page and in the admin footer,
and the consultancy — not the brand — holds the copyright.

The name and URL are `LaunchSetting.parentCompanyName` / `parentCompanyUrl`, so changing them is a
row edit. They are `NOT NULL`; anything creating a `LaunchSetting` must populate them.

Note that JHipster's `global.json` keeps `footer` as a **top-level** key, not a member of `global`.
Putting it under `global` compiles, builds and passes every test, then renders
`translation-not-found[...]` on screen.

## Conventions

- Domain services are named apart from JHipster's generated CRUD services, which own the obvious
  names: `CaptureEventRecorder`, `WaitlistCaptureService`, `MetricRollupEngine`, `MetricQueryService`.
- Public payload DTOs are hand-written records, not the generated entity DTOs — so adding an internal
  field to an entity cannot silently publish it.
- The public API is `/api/public/**` and anonymous; everything else is authenticated, and
  `/api/admin/**` requires `ROLE_ADMIN`. **`/api/register`, `/api/activate` and the password-reset
  endpoints are deliberately not permitted** — there is one privileged account, seeded by Liquibase
  and given its password from `ABOFONSA_ADMIN_PASSWORD`. The tests for them are `@Disabled`, not
  deleted, and the login page's links to them are removed.
- Rollups are a cache, never a source of truth. Everything is recomputable from `capture_event` and
  `waitlist_signup`; if a rollup disagrees with the raw log, the log wins.

## Configuration

| Variable                         | Purpose                                                                                                                                               |
| -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| `ABOFONSA_ADMIN_PASSWORD`        | Applied to the seeded `admin` account at every boot. Unset leaves the development credential and logs a warning.                                      |
| `ABOFONSA_HASH_SALT`             | Salts the visitor and IP hashes. **Set it per environment** — unsalted, the whole IPv4 space can be hashed in minutes and the protection is illusory. |
| `ABOFONSA_PUBLIC_BASE_URL`       | Where opt-in and unsubscribe links point. Must be the address the public reaches.                                                                     |
| `ABOFONSA_WAITLIST_MAX_PER_HOUR` | Submissions per client per hour before 429. Default 5.                                                                                                |
