package com.smartfactory.common.payloads.assembly_service;

import java.time.Instant;

public record VehicleAssembledPayload(
        String vehicleId,
        String orderId,
        String vehicleModel,
        String productionLine,
        Instant assembledAt) {
}
