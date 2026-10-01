package com.smartfactory.order.control.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartfactory.common.enums.BatteryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BmwM5ValidatorTest {

    private BmwM5Validator validator;

    @BeforeEach
    void setUp() {
        validator = new BmwM5Validator();
    }

    @ParameterizedTest
    @ValueSource(strings = { "bmw_m5", "BmW_m5", "Bmw_M5", "bmw_M5" })
    void shouldSupportBmwI4CaseInsensitive(final String vehicleModel) {
        assertTrue(validator.supports(vehicleModel));
    }

    @ParameterizedTest
    @ValueSource(strings = { "bmw_i5", "tesla_model_3", "audi_e_tron", "" })
    void shouldNotSupportOtherVehicleModels(final String vehicleModel) {
        assertFalse(validator.supports(vehicleModel));
    }

    @Test
    void shouldBeValidForAllowedBatteryTypes() {
        assertTrue(validator.isValid(BatteryType.STANDARD_RANGE));
    }

    @Test
    void shouldBeInvalidForDisallowedBatteryTypes() {
        assertFalse(validator.isValid(BatteryType.PERFORMANCE));
        assertFalse(validator.isValid(BatteryType.LONG_RANGE));
    }
}