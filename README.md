# Health Connect — launch preview site

The pre-launch site for **Health Connect**, the care network from Abofonsa BridgeCare, counting down
to **1 February 2027**.

A JHipster 9.1.0 monolith: Spring Boot 4 (Java 21) + Angular 21, PostgreSQL, generated from
[`abofonsa-preview.jdl`](abofonsa-preview.jdl). JHipster's own generated README is kept at
[`docs/JHIPSTER-README.md`](docs/JHIPSTER-README.md).

## What it does

**The public page** (`/`) — one scroll, everything driven from the database rather than markup:

- a countdown to the launch instant held in `LaunchSetting`, not a constant in the bundle
- waitlist capture with double opt-in, a honeypot, a dwell-time check and per-client rate limiting
- the six real care services and the PEAR / PAWPAW / MELON plans, at their real GHS pricing
- a pledge teaser that **hands off** to [fund.abofonsa.com](https://fund.abofonsa.com) and counts the
  outbound click — this application never takes a payment
- roadmap, social and contact links
- English, French, Spanish and German chrome

**The admin dashboard** (`/admin/waitlist-dashboard`, `ROLE_ADMIN`):

- KPI tiles with 7-day-over-7-day movement
- a line chart of any metric at hour / day / week / month / quarter / year zoom, optionally split by
  campaign source
- the captured emails, paginated and filterable
- CSV export of both the emails and whatever the chart is currently showing, each recorded in an
  audit table

## Running it

All three pins below are required — see [CLAUDE.md](CLAUDE.md) for what happens without them.

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64   # JHipster rejects JDK 26
nvm use                                               # .nvmrc → 24.18.0

docker compose -f src/main/docker/services.yml up -d  # Postgres on 5432
ABOFONSA_ADMIN_PASSWORD='choose-one' ./mvnw spring-boot:run
```

Then open <http://localhost:8083/> for the public page, or sign in at `/login` as `admin` with the
password you exported and go to **Administration → Waitlist dashboard**.

For frontend hot-reload, run `npm start` alongside; it proxies the API to 8083.

## Testing

```bash
./mvnw verify                                       # 567 tests: unit + Testcontainers integration
npm test && npm run lint && npm run webapp:build:prod
```

## Configuration

| Variable                         | Default                 | Purpose                                                                                                   |
| -------------------------------- | ----------------------- | --------------------------------------------------------------------------------------------------------- |
| `ABOFONSA_ADMIN_PASSWORD`        | _(unset)_               | Applied to the seeded `admin` account at boot. Unset keeps the development credential and logs a warning. |
| `ABOFONSA_HASH_SALT`             | `dev-only-unsafe-salt`  | Salts the visitor and IP hashes. **Must be set per environment.**                                         |
| `ABOFONSA_PUBLIC_BASE_URL`       | `http://localhost:8083` | Where opt-in and unsubscribe links point.                                                                 |
| `ABOFONSA_WAITLIST_MAX_PER_HOUR` | `5`                     | Submissions per client per hour before `429`.                                                             |

Mail is optional: with no SMTP server configured the confirmation link is logged instead of sent, and
the opt-in flow still completes.

## Privacy

The page records what it needs to answer "how is the launch going" and nothing that identifies a
person beyond the address they typed:

- IP and session hashes are **salted SHA-256, truncated**; the session hash mixes in the UTC date, so
  it rotates at midnight and `UNIQUE_VISITORS` is a daily-distinct count rather than a durable
  identifier
- only the referrer's **host** is stored, never the full URL
- consent is required, not implied, and unsubscribing is one click from any email

## Related repositories

- **`hc-abofonsa-web`** — the real marketing site, and the source of truth for service and plan copy.
  Service slugs match, so the two reconcile without a mapping table.
- **`hc-crowdfund-app`** — the crowdfunding platform at `fund.abofonsa.com`, which owns pledging end
  to end: identity, payment, vouchers and certificates.

## Layout

```
src/main/java/net/jojoaddison/abofonsa/preview/
  domain/ repository/ service/ web/rest/   JHipster's shape, unchanged
  service/PublicContentService             the launch page's payload, cached
  service/WaitlistCaptureService           capture, dedupe, opt-in, throttle
  service/MetricRollupEngine               derives rollups from the raw event log
  service/MetricQueryService               reads them back as chartable series
src/main/webapp/app/
  launch/                                  the public page
  admin/waitlist-dashboard/                the dashboard and its chart
docs/design/abofonsa-countdown.html        the design the page follows
plan.md                                    build log: phases, decisions, traps
```
