#!/bin/bash
set -euo pipefail

sudo dnf update -y
sudo dnf install -y java-21-amazon-corretto

sudo mkdir -p /opt/kiaevplatform
sudo chown ec2-user:ec2-user /opt/kiaevplatform

java -version
