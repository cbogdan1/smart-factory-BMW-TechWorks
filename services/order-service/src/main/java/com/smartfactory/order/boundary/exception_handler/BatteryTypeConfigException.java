package com.smartfactory.order.boundary.exception_handler;

import static com.smartfactory.common.exception.OrderErrorCodes.INVALID_BATTERY_CONFIGURATION;

import java.io.Serial;

import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.common.exception.BusinessException;

public class BatteryTypeConfigException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 5169976639754226537L;

    public BatteryTypeConfigException(final BatteryType batteryType) {
        super("Battery type is not compatible with vehicle model: " + batteryType.toString(),
                INVALID_BATTERY_CONFIGURATION, 400);
    }

    public BatteryTypeConfigException(final String message) {

        super("Wrong parameter, may be null: " + message, INVALID_BATTERY_CONFIGURATION, 400);
    }
}
