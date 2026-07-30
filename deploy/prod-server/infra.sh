#!/bin/bash
# One-time setup: create the external Docker network compose.yml expects. Mirrors the other apps'
# infra.sh scripts under ~/webroot, which pre-create a named network rather than let compose
# generate a default one — a shared, predictable name is what lets the monitoring stack attach.
#
# The `monitoring` network is NOT created here: it belongs to ~/webroot/00-admin/monitoring and
# already exists on this host. If it does not, that stack has to come up first.
set -euo pipefail

if docker network inspect abofonsapreviewnet >/dev/null 2>&1; then
  echo "abofonsapreviewnet already exists"
else
  docker network create abofonsapreviewnet
  echo "created abofonsapreviewnet"
fi

if ! docker network inspect monitoring >/dev/null 2>&1; then
  echo "WARNING: the shared 'monitoring' network is missing. Bring up ~/webroot/00-admin/monitoring" >&2
  echo "         first, or the app service will fail to start." >&2
fi
