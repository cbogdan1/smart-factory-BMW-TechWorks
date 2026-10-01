package com.smartfactory.order.control.validator;

import com.smartfactory.common.enums.BatteryType;

public interface VehicleValidationStrategy {
    boolean supports(String vehicleModel);

    boolean isValid(BatteryType batteryType);
}
