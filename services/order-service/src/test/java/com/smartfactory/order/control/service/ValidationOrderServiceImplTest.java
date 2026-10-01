package com.smartfactory.order.control.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.order.boundary.exception_handler.BatteryTypeConfigException;
import com.smartfactory.order.boundary.exception_handler.VehicleModelNotFoundException;
import com.smartfactory.order.control.validation_service.ValidationServiceImpl;
import com.smartfactory.order.control.validator.BmwI4Validator;
import com.smartfactory.order.control.validator.BmwIXValidator;
import com.smartfactory.order.control.validator.BmwM5Validator;
import com.smartfactory.order.control.validator.VehicleValidationStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ValidationOrderServiceImplTest {

    private ValidationServiceImpl validationService;

    private List<Map.Entry<String, BatteryType>> validMatrix;
    private List<Map.Entry<String, BatteryType>> invalidMatrix;

    @BeforeEach
    void setUp() {
        final List<VehicleValidationStrategy> validators = new ArrayList<>();
        validators.add(new BmwI4Validator());
        validators.add(new BmwIXValidator());
        validators.add(new BmwM5Validator());
        validationService = new ValidationServiceImpl(validators);

        validMatrix = new ArrayList<>();
        validMatrix.add(new AbstractMap.SimpleEntry<>("BMW_IX", BatteryType.PERFORMANCE));
        validMatrix.add(new AbstractMap.SimpleEntry<>("BMW_IX", BatteryType.LONG_RANGE));
        validMatrix.add(new AbstractMap.SimpleEntry<>("bmw_m5", BatteryType.STANDARD_RANGE));
        validMatrix.add(new AbstractMap.SimpleEntry<>("bmw_i4", BatteryType.LONG_RANGE));
        validMatrix.add(new AbstractMap.SimpleEntry<>("bmw_i4", BatteryType.STANDARD_RANGE));

        invalidMatrix = new ArrayList<>();
        invalidMatrix.add(new AbstractMap.SimpleEntry<>("BMW_IX", BatteryType.STANDARD_RANGE));
        invalidMatrix.add(new AbstractMap.SimpleEntry<>("bmw_m5", BatteryType.LONG_RANGE));
        invalidMatrix.add(new AbstractMap.SimpleEntry<>("bmw_m5", BatteryType.PERFORMANCE));
        invalidMatrix.add(new AbstractMap.SimpleEntry<>("bmw_i4", BatteryType.PERFORMANCE));
    }

    @Test
    void shouldThrowVehicleModelExceptionWhenVehicleModelIsNull() {
        final VehicleModelNotFoundException exception = assertThrows(VehicleModelNotFoundException.class,
                () -> validationService.validate(null, BatteryType.LONG_RANGE));

        assertEquals("Unsupported vehicle model: Vehicle model cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowBatteryTypeExceptionWhenBatteryTypeIsNull() {
        final BatteryTypeConfigException exception =
                assertThrows(BatteryTypeConfigException.class, () -> validationService.validate("null", null));

        assertEquals("Wrong parameter, may be null: Battery type cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowVehicleModelExceptionWhenModelDoesNotExist() {
        final VehicleModelNotFoundException exception = assertThrows(VehicleModelNotFoundException.class,
                () -> validationService.validate("bmw_unknown_model", BatteryType.LONG_RANGE));

        assertEquals("Unsupported vehicle model: bmw_unknown_model", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = BatteryType.class, names = { "PERFORMANCE", "LONG_RANGE" })
    void shouldThrowBatteryTypeExceptionWhenBatteryTypeIsNotValid(final BatteryType invalidBatteryType) {
        final String vehicleModel = "bmw_m5";

        final BatteryTypeConfigException exception = assertThrows(BatteryTypeConfigException.class,
                () -> validationService.validate(vehicleModel, invalidBatteryType));

        assertEquals("Battery type is not compatible with vehicle model: " + invalidBatteryType,
                exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = BatteryType.class, names = { "PERFORMANCE", "LONG_RANGE" })
    void shouldNotThrowAnythingWhenModelAndBatteryExist(final BatteryType validBatteryType) {
        final String vehicleModel = "BMW_IX";

        assertDoesNotThrow(() -> validationService.validate(vehicleModel, validBatteryType));
    }

    @Test
    void shouldHandleNullValidatorsList() {
        final ValidationServiceImpl emptyService = new ValidationServiceImpl(null);

        final VehicleModelNotFoundException exception = assertThrows(VehicleModelNotFoundException.class,
                () -> emptyService.validate("bmw_m5", BatteryType.LONG_RANGE));

        assertEquals("Unsupported vehicle model: bmw_m5", exception.getMessage());
    }

    @Test
    void shouldAcceptAllValidMatrixCombinations() {
        validMatrix.forEach(
                entry -> assertDoesNotThrow(() -> validationService.validate(entry.getKey(), entry.getValue())));
    }

    @Test
    void shouldRejectAllInvalidMatrixCombinations() {
        invalidMatrix.forEach(entry -> {
            final BatteryTypeConfigException exception = assertThrows(BatteryTypeConfigException.class,
                    () -> validationService.validate(entry.getKey(), entry.getValue()));

            assertEquals("Battery type is not compatible with vehicle model: " + entry.getValue(),
                    exception.getMessage());
        });
    }

}