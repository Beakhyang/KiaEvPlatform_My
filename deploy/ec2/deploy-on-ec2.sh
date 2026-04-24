#!/bin/bash
set -euo pipefail

APP_DIR=/opt/kiaevplatform
SERVICE_NAME=kiaevplatform
JAR_SOURCE="${1:-./KiaEvPlatform_My-0.0.1-SNAPSHOT.jar}"
ENV_SOURCE="${2:-./app.env}"
SERVICE_SOURCE="${3:-./kiaevplatform.service}"

if [[ ! -f "$JAR_SOURCE" ]]; then
  echo "Jar file not found: $JAR_SOURCE" >&2
  exit 1
fi

if [[ ! -f "$ENV_SOURCE" ]]; then
  echo "Environment file not found: $ENV_SOURCE" >&2
  exit 1
fi

if [[ ! -f "$SERVICE_SOURCE" ]]; then
  echo "Service file not found: $SERVICE_SOURCE" >&2
  exit 1
fi

sudo install -d -m 755 "$APP_DIR"
sudo install -m 644 "$JAR_SOURCE" "$APP_DIR/app.jar"
sudo install -m 600 "$ENV_SOURCE" "$APP_DIR/app.env"
sudo install -m 644 "$SERVICE_SOURCE" "/etc/systemd/system/${SERVICE_NAME}.service"

sudo systemctl daemon-reload
sudo systemctl enable "$SERVICE_NAME"
sudo systemctl restart "$SERVICE_NAME"
sudo systemctl status "$SERVICE_NAME" --no-pager
