package com.smartfactory.order.control.validation_service;

import java.util.List;
import java.util.Optional;

import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.order.boundary.exception_handler.BatteryTypeConfigException;
import com.smartfactory.order.boundary.exception_handler.VehicleModelNotFoundException;
import com.smartfactory.order.control.validator.VehicleValidationStrategy;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ValidationServiceImpl implements ValidationService {

    private final List<VehicleValidationStrategy> validators;

    //runtime bean injection -> warning compile time( quarkus injects at compile time unlike spring boot trough reflection)
    //@All is not standard CDI, and it will gather beans dynamically at runtime
    //Instance<VehicleValidationStrategy> for standard CDI and no warning but stream is needed to return a list from it
    public ValidationServiceImpl(@All final List<VehicleValidationStrategy> validators) {
        this.validators = validators;
    }

    @Override
    public void validate(final String vehicleModel, final BatteryType batteryType) {
        validateNotNullInputs(vehicleModel, batteryType);

        final VehicleValidationStrategy validator = findValidator(vehicleModel);

        validateBatteryType(validator, batteryType);

    }

    private void validateNotNullInputs(final String vehicleModel, final BatteryType batteryType)
            throws VehicleModelNotFoundException, BatteryTypeConfigException {
        if (vehicleModel == null) {
            throw new VehicleModelNotFoundException("Vehicle model cannot be null");
        }
        if (batteryType == null) {
            throw new BatteryTypeConfigException("Battery type cannot be null");
        }
    }

    private VehicleValidationStrategy findValidator(final String vehicleModel) throws VehicleModelNotFoundException {
        return Optional.ofNullable(validators).orElseGet(List::of).stream()
                .filter(validator -> validator.supports(vehicleModel)).findFirst()
                .orElseThrow(() -> new VehicleModelNotFoundException(vehicleModel));
    }

    private void validateBatteryType(final VehicleValidationStrategy validator, final BatteryType batteryType)
            throws BatteryTypeConfigException {
        if (!validator.isValid(batteryType)) {
            throw new BatteryTypeConfigException(batteryType);
        }
    }

}
