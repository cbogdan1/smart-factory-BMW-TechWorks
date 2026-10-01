# Data Warehouse Service ('dwh-service')

## Overview
The Data Warehouse Service acts as the central analytical and event history repository for the Smart Factory.

It asynchronously consumes domain events produced by other microservices via Kafka, guarantess idempotent event storage and exposes REST enpoints to query historical metrics, event streams, and vehicle timelines.

## Capabilities

1. Event Store Persistence - consumes and arhives all incoming system events into an append-only store for auditing and replay capabilities.
2. Vehicle Timeline - tracks, filters and orders chronological events associated with specific vehicle IDs (correlationId) across their entire lifecyle.
3. Idempotency - ensures duplicate event message on Kafka topics do not lead to duplicated records in the database.

## Achitecture
*TODO*

## REST API Specification
*TODO*