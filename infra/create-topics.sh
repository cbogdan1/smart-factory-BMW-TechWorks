#!/usr/bin/env bash
set -euo pipefail

# Pre-creates the 17 business topics. Runs automatically via the one-shot
# `kafka-init` service in docker-compose (after Kafka becomes healthy). With
# KAFKA_AUTO_CREATE_TOPICS_ENABLE=true this is optional, but explicit creation
# gives control over partitions.
#
# This script runs INSIDE a Kafka container and connects to the broker's
# internal listener. Override the target with KAFKA_BOOTSTRAP if needed.
BOOTSTRAP="${KAFKA_BOOTSTRAP:-kafka:29092}"

TOPICS=(
  order-created order-status-updated order-completed
  production-planned planning-kpi-updated
  parts-reserved parts-shortage-detected inventory-low-stock
  parts-delivered procurement-metrics-updated
  material-requested material-delivered
  vehicle-stage-advanced vehicle-assembled
  quality-approved quality-rework-required quality-failed
)

for t in "${TOPICS[@]}"; do
  /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server "$BOOTSTRAP" --create --if-not-exists \
    --topic "$t" --partitions 3 --replication-factor 1
done

/opt/kafka/bin/kafka-topics.sh --bootstrap-server "$BOOTSTRAP" --list

