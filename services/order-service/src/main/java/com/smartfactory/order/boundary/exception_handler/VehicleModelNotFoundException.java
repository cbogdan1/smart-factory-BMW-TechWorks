package com.smartfactory.order.boundary.exception_handler;

import static com.smartfactory.common.exception.OrderErrorCodes.INVALID_VEHICLE_MODEL;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class VehicleModelNotFoundException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 3318891911878315942L;

    public VehicleModelNotFoundException(final String vehicleModel) {
        super("Unsupported vehicle model: " + vehicleModel, INVALID_VEHICLE_MODEL, 400);
    }
}
