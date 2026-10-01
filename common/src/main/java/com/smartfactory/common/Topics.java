package com.smartfactory.common;

/**
 * Central registry of the Kafka topic names shared across all services.
 *
 * <p>Reference these constants from producers/consumers instead of hard-coding
 * strings, so a rename happens in exactly one place. Keep this list in sync with
 * {@code infra/create-topics.sh}.
 */
public final class Topics {

    // order-service
    public static final String ORDER_CREATED = "order-created";
    public static final String ORDER_STATUS_UPDATED = "order-status-updated";
    public static final String ORDER_CREATED_DLQ = "order-created-dlq";
    public static final String ORDER_COMPLETED = "order-completed";
    // planning-service
    public static final String PRODUCTION_PLANNED = "production-planned";
    public static final String PLANNING_KPI_UPDATED = "planning-kpi-updated";
    public static final String PRODUCTION_PLANNED_DLQ = "production-planned-dlq";
    // inventory-service
    public static final String PARTS_RESERVED = "parts-reserved";
    public static final String PARTS_SHORTAGE_DETECTED = "parts-shortage-detected";
    public static final String INVENTORY_LOW_STOCK = "inventory-low-stock";
    // procurement-service
    public static final String PARTS_DELIVERED = "parts-delivered";
    public static final String PROCUREMENT_METRICS_UPDATED = "procurement-metrics-updated";
    // assembly-service
    public static final String MATERIAL_REQUESTED = "material-requested";
    public static final String MATERIAL_DELIVERED = "material-delivered";
    // agv-service
    public static final String VEHICLE_STAGE_ADVANCED = "vehicle-stage-advanced";
    public static final String VEHICLE_STAGE_ADVANCED_DLQ = "vehicle-stage-advanced-dlq";
    public static final String VEHICLE_ASSEMBLED = "vehicle-assembled";
    public static final String VEHICLE_ASSEMBLED_DLQ = "vehicle-assembled-dlq";
    // quality-service
    public static final String QUALITY_APPROVED = "quality-approved";
    public static final String QUALITY_REWORK_REQUIRED = "quality-rework-required";
    public static final String QUALITY_FAILED = "quality-failed";
    private Topics() {
    }
}

