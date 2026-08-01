# Deployment

Deploys the launch preview site to **webserver**, at `~/webroot/01-healthconnect/abofonsa-preview/`,
following the conventions that host already uses (see the sibling `hc-abofonsa-web` and
`hc-crowdfund-app`): one external Docker network per app, loopback-only published ports, host nginx
owning TLS, container names prefixed per app.

Everything in `prod-server/` is deployed **as-is** — the files in this repository are the source of
truth, not a starting point to be edited on the server. A change made only on the host is lost at
the next deploy and invisible to review.

> **Live at https://abofonsa.com and https://www.abofonsa.com** since 31 July 2026, first deployed
> at commit `8cc5490`. One Let's Encrypt certificate covers both names and renews automatically.
>
> Re-running `--bootstrap` against this install is refused: it will not overwrite the generated
> `.env`, which is the only copy of the four secrets.

## What gets deployed

**One image.** This is a JHipster monolith: `-Pprod` compiles the Angular bundle into the jar's
static resources, so Spring Boot serves the launch page, the public API and the admin dashboard from
a single process. There is no separate web container, and nginx has exactly one upstream.

The image is built by **jib**, not a Dockerfile — JHipster generates no Dockerfile, and the
`jib-maven-plugin` configuration in `pom.xml` is the image definition.

```
deploy/
  build.sh                        build + push the image (jib, -Pprod, tests run)
  deploy.sh                       the whole deployment: preflight → build → ship → restart → verify
  prod-server/                    deployed verbatim to the server
    compose.yml                   app + postgres, loopback-only, hardened
    .env.example                  template; the real .env is generated on the server
    abofonsa-preview.conf         host nginx site (pre-certbot, HTTP only)
    infra.sh                      one-time: create the external network
    start                         pull the tagged image and recreate the stack
    backup.sh                     nightly pg_dump, 14-day retention
.github/workflows/
  publish.yml                     the github channel: same image, built on a GitHub runner
```

## Channels

`--channel` decides **where the image comes from**. It changes nothing else: the same
`compose.yml`, the same `.env`, the same verification.

| Channel | Image | Built by |
| --- | --- | --- |
| `private` (default) | `docker.jojoaddison.net/abofonsa-preview` | `build.sh`, on your machine |
| `github` | `ghcr.io/<owner>/abofonsa-preview` | `.github/workflows/publish.yml`, on a GitHub runner |

```bash
./deploy/deploy.sh                                   # private: build here, push, deploy
./deploy/deploy.sh --channel github                  # github: deploy what Actions already built
TAG=<sha> ./deploy/deploy.sh --channel github        # roll back to any published tag
```

The channel is stored on the server as `REGISTRY` in `.env`, and `deploy.sh` rewrites that line —
after showing you the change and asking — whenever the requested channel differs from what the host
is pointing at. So the same commit can be served from either registry without editing anything on
the host by hand. `--github` is shorthand for `--channel github`.

`--channel github` never builds. Falling through to `build.sh` would push a locally built image over
the one Actions published *under the same tag*, which is the drift the channel exists to prevent.
Instead the script checks the tag is really published (`docker manifest inspect`) before it touches
the server, warns if HEAD is not on a remote branch — an unpushed commit is one Actions never saw —
and after the restart confirms the running container's image actually came from `$REGISTRY`. The
same tag can exist in both registries, so the published-tag check alone would pass while the
container still ran the other channel's image.

### What publish.yml does

Runs on every push to `main`, on `v*` tags, and on demand. It builds with **jib** — not
`docker/build-push-action`, because JHipster generates no Dockerfile and the `jib-maven-plugin`
configuration in `pom.xml` *is* the image definition — and it runs `-Pprod verify` first, exactly as
`build.sh` does. This repository has no separate CI workflow, so that is the only gate an image
passes through.

It publishes `:<short-sha>`, plus the bare 7-character prefix when git's abbreviation length differs
(a shallow runner clone can abbreviate differently from your full local one, and `deploy.sh` asks
for whatever *your* checkout produces). `:latest` moves only on a default-branch build, never on a
`workflow_dispatch` run of a feature branch. A pushed `v1.2.3` tag becomes an image tag of the same
name.

Authentication is `GITHUB_TOKEN` with `packages: write` — no PAT, no repository secret, and nothing
in the workflow can reach the production server. Publishing and releasing stay separate steps.

### Before the github channel works

Two things this repository cannot do for itself:

1. **The first push has to land.** `origin` is `github.com:kojoampia/hc-abofonsa`, and the GHCR
   namespace follows from that owner — `ghcr.io/kojoampia/abofonsa-preview`, which is also
   `deploy.sh`'s default and what the sibling `hc-crowdfund-app` uses. Until a push to `main`
   completes a workflow run, there is nothing in GHCR to deploy. Override the namespace for a fork
   or an organisation with `GITHUB_REGISTRY=ghcr.io/<owner>`.
2. **A GHCR package is private by default.** Either make it public (Packages → `abofonsa-preview` →
   Package settings → Change visibility) or give the server a credential:
   `ssh webserver 'docker login ghcr.io -u <user>'` with a PAT carrying `read:packages`. `deploy.sh`
   checks for that credential on the host and warns, because the alternative is discovering it from
   a failed pull with the stack already stopped.

## First-time install

DNS for the chosen host must already point at webserver before the TLS step.

```bash
# 1. Everything except nginx and TLS. Generates all four secrets ON THE SERVER.
./deploy/deploy.sh --bootstrap

# 2. Once you are happy the app is healthy, publish it.
./deploy/deploy.sh --bootstrap --with-nginx --with-tls
```

`--bootstrap` refuses to run if `compose.yml` already exists on the server, and refuses to overwrite
an existing `.env`.

Read the generated admin password once, then store it in a password manager:

```bash
ssh webserver "grep ABOFONSA_ADMIN_PASSWORD ~/webroot/01-healthconnect/abofonsa-preview/.env"
```

## Routine deploys

```bash
./deploy/deploy.sh                              # build, ship, restart, verify
./deploy/deploy.sh --verify-only                # mutate nothing, just run the checks
TAG=<previous-sha> ./deploy/deploy.sh --skip-build   # roll back to an already-pushed image
./deploy/deploy.sh --recover                    # compose.yml went missing but .env survived
```

Every mutating step is announced before it runs and prompts unless `--yes` is passed — certbot
included. It did originally ignore `--yes`, on the reasoning that it talks to a rate-limited third
party about a real domain, but a `read` prompt in a non-interactive shell gets EOF and silently
skips, which turns an explicit `--with-tls` into a no-op. The guard is now a check rather than a
question: every hostname must resolve to the server's own IP, or the step fails without calling
certbot. That verifies the precondition the prompt was asking a human to vouch for, and refuses
rather than spending quota on a name that could not have passed.

## Secrets

All four are generated by `openssl rand` **running on the server** during bootstrap, so they never
exist on the deploying machine, in its shell history, or on the wire.

| Variable | Generate with | Notes |
| --- | --- | --- |
| `POSTGRES_PASSWORD` | `openssl rand -base64 24` | No trust auth here, unlike the dev compose file. |
| `JWT_BASE64_SECRET` | `openssl rand -base64 64` | HS512 needs ≥ 64 bytes. Rotating it signs the operator out, which is the correct response to a suspected leak. |
| `ABOFONSA_ADMIN_PASSWORD` | `openssl rand -base64 24` | Applied to the seeded `admin` account at every boot. |
| `ABOFONSA_HASH_SALT` | `openssl rand -base64 32` | Set once, then leave it. Changing it resets every rate-limit counter and makes `UNIQUE_VISITORS` discontinuous. |

**There is no fallback if `JWT_BASE64_SECRET` is unset.** The sample key lives in the
`secret-samples` profile, which belongs to the **dev** profile group and is committed to this
repository. `SPRING_PROFILES_ACTIVE=prod` does not activate it, so production supplies its own key
or fails to start — and failing to start is the safe outcome.

`compose.yml` uses `${VAR:?message}` for all four, so a missing value fails at
`docker compose config` with a message naming the variable, rather than booting something insecure.

## Mail

Optional, and the deploy succeeds without it. With `SMTP_HOST` empty, waitlist confirmation links
are written to the application log instead of sent and the double opt-in flow still completes —
fine for a first deploy, **not fine for a live waitlist**, because nobody outside the server can
then confirm their own signup. `deploy.sh --bootstrap` warns about this explicitly.

## Backups

`backup.sh` runs `pg_dump --clean --if-exists` into `backups/`, keeps 14 days, and writes the dump
`0600` — it contains other people's email addresses. It refuses to rotate old backups when the new
dump is suspiciously small, because a `pg_dump` that fails mid-stream still exits 0 through a pipe.

Add to root's crontab on the server, alongside the existing cert-renewal entry:

```cron
15 3 * * * ~/webroot/01-healthconnect/abofonsa-preview/backup.sh >> /var/log/abofonsa-preview-backup.log 2>&1
```

A backup nobody has restored is a hypothesis. Do one restore drill into a scratch database before
this carries real signups.

## Verification

`deploy.sh` finishes by checking, on every deploy:

- `/management/health` is `UP` on the container's loopback port — internally first, so "the app is
  broken" and "the proxy is misconfigured" stay distinguishable
- `/api/public/content` returns a launch payload, and reports how many seeded services it contains
  (the seeds carry `contextFilter="!test"`; had they been skipped, the page would render empty —
  which looks fine to a health check and wrong to every visitor)
- `/api/admin/**` answers **401** to an anonymous caller
- `/api/register` is closed
- the public URL responds, if DNS and TLS are in place

These artifacts were validated locally by bringing up **this exact `compose.yml`** against the
jib-built production image:

| Check | Result |
| --- | --- |
| `docker compose config` with no `.env` | fails, naming each missing secret |
| `docker compose config` with `.env` | resolves |
| `nginx -t` on `abofonsa-preview.conf` | passes |
| Stack up, app healthy | yes |
| `/api/public/content` under `SPRING_PROFILES_ACTIVE=prod` | 6 services, 3 plans, 3 tiers, `launchAt 2027-02-01T00:00:00Z` |
| `/` | 200, serving the launch page's real `<title>` |
| `/api/admin/metrics/summary`, `/api/register` anonymous | 401 |
| `ABOFONSA_ADMIN_PASSWORD` applied | configured password 200, seeded `admin`/`admin` 401 |
| `backup.sh` | 18 tables dumped with data, `0600` |

One bug was found by doing this rather than reasoning about it: **postgres:18 requires the data
volume at `/var/lib/postgresql`, not `/var/lib/postgresql/data`**, and refuses to start otherwise.
The dev compose file never hits it because its volume line is commented out, so this would have
failed on the very first real deploy.

### What the first real deploy then found

Three more, none of which local testing could have surfaced:

- **The server's disk was 100% full** — 85 MB free of 47 GB — so the registry answered `500` to the
  blob upload. `docker image prune -a` recovered 10 GB (to 78%). At that level Postgres and Mongo
  across *every* app on the host were at risk of failing writes, not just this deploy. 47 GB with
  this many services will fill again; a scheduled prune is worth adding.
- **`build.sh` rejected a valid JDK 21.** `sed 's/.*"\([0-9]*\).*/\1/'` is greedy, so `.*"` ran to
  the *closing* quote of `"21.0.11"` and captured an empty string.
- **`--yes` would have silently skipped TLS.** The certbot step used a `read` prompt deliberately
  made non-skippable; in a non-interactive shell that reads EOF and skips, turning an explicit
  `--with-tls` into a no-op. It now verifies each hostname resolves to the server's own IP instead —
  a stronger guard than a prompt, because it checks the precondition rather than asking a human to
  vouch for it, and it refuses rather than spending Let's Encrypt quota on a name that cannot pass.

## Host conventions this follows

- **Loopback-only ports.** The app publishes `127.0.0.1:8084`; host nginx with Certbot-managed TLS
  is the only thing on a public interface. 8080 and 8081 are taken on that host, and
  `hc-abofonsa-web` took 8082.
- **Postgres publishes nothing.** `backup.sh` reaches it with `docker exec`.
- **Two external networks.** `abofonsapreviewnet` is created by `infra.sh`; `monitoring` belongs to
  `~/webroot/00-admin/monitoring` and must already exist, or the app container will not start.
  `infra.sh` warns rather than creating someone else's network.
- **`name:` is pinned** in `compose.yml` so `--remove-orphans` can only touch this project's
  containers — relying on the directory-derived default once cost a sibling app an unrelated
  container.
- **Container hyphens, not underscores.** An underscore is illegal in a hostname, so a container
  named `abofonsa_preview_app` cannot be addressed over the compose network without Tomcat rejecting
  the request with a bare 400.
- **`/management` returns 404 at the proxy.** Health and metrics belong to the monitoring network,
  which reaches the container directly. Not proxying them is a second, independent reason they never
  become publicly reachable.

## Decisions to confirm before going live

- ~~**The domain.**~~ **Decided: the apex and www.** Before claiming them the deploy confirmed
  nothing was displaced — `abofonsa.conf` serves only `web.abofonsa.com`, the existing certificates
  covered only `web.` and `fund.`, and the apex previously answered 404 from nginx's default site.
  One certificate covers both names; www is served by the same block rather than redirected.
- **Mail is still not configured.** The site is live and accepting waitlist addresses, but with
  `SMTP_HOST` empty every confirmation link is written to the container log instead of sent — so
  nobody outside the server can complete a signup and every row sits at `PENDING`. This is the one
  thing that makes the live site not yet fit for its purpose.
- **Search indexing.** Unlike `hc-abofonsa-web`, this app has no indexing switch; it serves whatever
  the Angular build emits. If this preview should stay out of search results until launch, that
  needs adding before publication.
- **Pledge hand-off URLs** assume `https://fund.abofonsa.com/pledge?tier=<CODE>`. Confirm against the
  live crowdfunding routes — a wrong deep link sends backers to a 404 at the moment they were about
  to pledge.
- **Social links** are seeded inactive with placeholder URLs. Set the real ones and flip `active`.
