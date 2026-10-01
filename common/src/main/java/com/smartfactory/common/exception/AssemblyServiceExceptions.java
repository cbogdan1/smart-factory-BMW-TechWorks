package com.smartfactory.common.exception;

import org.apache.kafka.common.protocol.types.Field;

public class AssemblyServiceExceptions {
    // VehicleProduction
    public static final String VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE = "VEHICLE_PRODUCTION_NOT_FOUND";
    public static final String VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE = "Vehicle production record with id %s was not found";

    public static final String INVALID_VEHICLE_STATUS_ERROR_CODE = "INVALID_VEHICLE_STATUS";
    public static final String INVALID_VEHICLE_STATUS_ERROR_MESSAGE = "Invalid vehicle production status for vehicle with id = %s";

    public static final String INVALID_STAGE_TRANSITION_ERROR_CODE = "INVALID_STAGE_TRANSITION";
    public static final String INVALID_STAGE_TRANSITION_ERROR_MESSAGE = "Invalid production stage transition from %s to %s";

    public static final String NULL_VEHICLE_ASSEMBLED_PAYLOAD_ERROR_CODE = "NULL_VEHICLE_ASSEMBLED_PAYLOAD";
    public static final String NULL_VEHICLE_ASSEMBLED_PAYLOAD_ERROR_MESSAGE = "Null vehicle assembled payload";

    // MaterialRequested
    public static final String NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_CODE = "NULL_MATERIAL_REQUESTED_PAYLOAD";
    public static final String NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_MESSAGE = "Null material requested payload";

    // ProductionPlanned
    public static final String NULL_PRODUCTION_PLANNED_EVENT_ERROR_CODE = "NULL_PRODUCTION_PLANNED_EVENT";
    public static final String NULL_PRODUCTION_PLANNED_EVENT_ERROR_MESSAGE = "Event must not be null";

    //PartsReserved
    public static final String NULL_PARTS_RESERVED_EVENT_ERROR_CODE = "NULL_PARTS_RESERVED_EVENT";
    public static final String NULL_PARTS_RESERVED_EVENT_ERROR_MESSAGE = "Event must not be null";

    // QualityReworkRequired event validation
    public static final String NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_CODE = "NULL_QUALITY_REWORK_REQUIRED_EVENT";
    public static final String NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_MESSAGE = "Event must not be null";

    // QualityReworkRequired business validation
    public static final String INVALID_REWORK_STAGE_ERROR_CODE = "INVALID_REWORK_STAGE_ERROR_CODE";
    public static final String INVALID_REWORK_STAGE_ERROR_MESSAGE = "Cannot rework at stage %s";

    public static final String EVENT_ALREADY_PROCESSED_ERROR_CODE = "EVENT_ALREADY_PROCESSED";
    public static final String EVENT_ALREADY_PROCESSED_ERROR_MESSAGE = "Event with id = %s already processed";
}
