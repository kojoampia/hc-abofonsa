#!/bin/bash
# Builds the application image and pushes it to the registry.
# Requires `docker login` against $REGISTRY_HOST to already be done.
#
#   ./deploy/build.sh                  # tag with the current short commit
#   TAG=v1.2.3 ./deploy/build.sh       # tag a release
#   REGISTRY=docker.jojoaddison.net/ns ./deploy/build.sh   # push somewhere else
#
# One image, not two: this is a JHipster monolith, and `-Pprod` compiles the Angular bundle into the
# jar's static resources. There is no separate web image to build.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

# Namespace prefix, not just the host — the image name is ${REGISTRY}/abofonsa-preview.
# Matches deploy/prod-server/.env.example so a hand-built image and a deploy.sh-built one land in
# the same place.
REGISTRY="${REGISTRY:-docker.jojoaddison.net}"
REGISTRY_HOST="${REGISTRY%%/*}"
IMAGE="${REGISTRY}/abofonsa-preview"

# Defaults to the current commit so every pushed image is traceable back to source.
TAG="${TAG:-$(git rev-parse --short HEAD)}"

# Both toolchains are pinned, and neither pin is cosmetic:
#   Java 21 — the box may default to JDK 26, which JHipster's maven-enforcer rejects outright
#             ("JHipster supports JDK 21 to 25"), so the build fails before it compiles anything.
#   Node 24 — package.json declares engines >= 24.16.0; the frontend build is part of -Pprod.
export JAVA_HOME="${JAVA_HOME_21:-/usr/lib/jvm/java-21-openjdk-amd64}"
if [[ ! -x "$JAVA_HOME/bin/java" ]]; then
  echo "error: no JDK 21 at $JAVA_HOME. Set JAVA_HOME_21 to one." >&2
  exit 1
fi

# `[^"]*` not `.*` before the quote: BRE is greedy, so `.*"` runs to the CLOSING quote of
# "21.0.11" and the capture that follows matches an empty string. The check then compares "" and
# rejects a perfectly good JDK with 'JDK  is outside the 21-25 range'.
java_major="$("$JAVA_HOME/bin/java" -version 2>&1 | sed -n '1s/[^"]*"\([0-9]*\).*/\1/p')"
if [[ "$java_major" -lt 21 || "$java_major" -gt 25 ]]; then
  echo "error: JDK $java_major is outside the 21-25 range JHipster's enforcer accepts." >&2
  exit 1
fi

if ! grep -q "$REGISTRY_HOST" ~/.docker/config.json 2>/dev/null; then
  echo "warning: no stored credential for $REGISTRY_HOST - the push will fail." >&2
  echo "         run: docker login $REGISTRY_HOST" >&2
fi

echo "Building ${IMAGE}:${TAG} (jib, -Pprod)"

# jib:build pushes straight to the registry without a local Docker daemon build — JHipster generates
# no Dockerfile, and the jib plugin configuration in pom.xml is the image definition.
#
# `verify` before it, not `package`: this is the last gate before an image goes to production, and
# skipping the tests here is how an image nobody ran the suite against ends up deployed.
./mvnw -ntp --batch-mode -Pprod verify jib:build \
  -Djib.to.image="${IMAGE}:${TAG}" \
  -Djib.to.tags=latest \
  -Djib.console=plain

echo "Done. Pushed:"
echo "  ${IMAGE}:${TAG}"
echo "  ${IMAGE}:latest"
