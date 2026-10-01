package com.smartfactory.common.dto.assembly;

import jakarta.validation.constraints.NotNull;

public record CreateVehicleProductionRequest(
        @NotNull(message = "Vehicle ID must not be null") String vehicleId,
        @NotNull(message = "Order ID must not be null") String orderId,
        @NotNull(message = "Vehicle model must not be null") String vehicleModel,
        String productionLine) {
}
