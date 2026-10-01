package com.smartfactory.order.control.validation_service;

import com.smartfactory.common.enums.BatteryType;

public interface ValidationService {
    void validate(String vehicleModel, BatteryType batteryType);
}
