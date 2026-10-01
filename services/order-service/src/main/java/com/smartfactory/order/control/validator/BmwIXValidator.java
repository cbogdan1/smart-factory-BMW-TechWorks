package com.smartfactory.order.control.validator;

import java.util.Set;

import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.common.enums.OrderVehicleModelType;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BmwIXValidator implements VehicleValidationStrategy {
    private static final Set<BatteryType> ALLOWED = Set.of(BatteryType.LONG_RANGE, BatteryType.PERFORMANCE);

    @Override
    public boolean supports(final String vehicleModel) {
        return vehicleModel.equalsIgnoreCase(OrderVehicleModelType.BMW_IX.name());
    }

    @Override
    public boolean isValid(final BatteryType batteryType) {
        return ALLOWED.contains(batteryType);
    }
}
