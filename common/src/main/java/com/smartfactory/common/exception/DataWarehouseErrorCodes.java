package com.smartfactory.common.exception;

public final class DataWarehouseErrorCodes {
    public static final String VEHICLE_NOT_FOUND = "VEHICLE_NOT_FOUND";
    public static final String TIMELINE_NOT_FOUND = "TIMELINE_NOT_FOUND";
    public static final String INVALID_VEHICLE_ID = "INVALID_VEHICLE_ID";
    public static final String INVALID_EVENT = "INVALID_EVENT";
    public static final String UNSUPPORTED_EVENT_TYPE = "UNSUPPORTED_EVENT_TYPE";
    public static final String EVENT_STORE_RETRIEVAL_FAILED = "EVENT_STORE_RETRIEVAL_FAILED";
    public static final String ANALYTICS_SUMMARY_FAILED = "ANALYTICS_SUMMARY_FAILED";
    public static final String EVENT_SERIALIZATION_FAILED = "EVENT_SERIALIZATION_FAILED";
    public static final String DASHBOARD_KPIS_FAILED = "DASHBOARD_KPIS_FAILED";

    private DataWarehouseErrorCodes() {
    }

}
