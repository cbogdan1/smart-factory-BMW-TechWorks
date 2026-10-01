package com.smartfactory.common.exception;

public final class PlanningServiceExceptions {
    public static final String INVALID_EVENT_TYPE = "INVALID_EVENT_TYPE";

    // order-service
    public static final String INVALID_ORDER_CREATED_EVENT = "INVALID_ORDER_CREATED_EVENT";

    // assembly-service
    public static final String INVALID_VEHICLE_STAGE_ADVANCED_EVENT = "INVALID_VEHICLE_STAGE_ADVANCED_EVENT";
    public static final String INVALID_STAGE_TRANSITION = "INVALID_STAGE_TRANSITION";

    // planning-service
    public static final String PLAN_NOT_FOUND = "PLAN_NOT_FOUND";
    public static final String NO_PRODUCTION_LINE_AVAILABLE = "NO_PRODUCTION_LINE_AVAILABLE";
    public static final String PRODUCTION_LINE_CAPACITY_EXCEEDED = "PRODUCTION_LINE_CAPACITY_EXCEEDED";
    public static final String PRODUCTION_LINE_NOT_FOUND = "PRODUCTION_LINE_NOT_FOUND";
}
