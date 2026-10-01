package com.smartfactory.common.exception;

public final class OrderErrorCodes {

    private OrderErrorCodes() {
    }

    public static final String ORDER_NOT_FOUND =
            "ORDER_NOT_FOUND";

    public static final String INVALID_STATUS_TRANSITION =
            "INVALID_STATUS_TRANSITION";

    public static final String INVALID_VEHICLE_MODEL =
            "INVALID_VEHICLE_MODEL";

    public static final String INVALID_BATTERY_CONFIGURATION =
            "INVALID_BATTERY_CONFIGURATION";
}