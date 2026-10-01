package com.smartfactory.common.dto.assembly;

import java.time.OffsetDateTime;

import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.enums.ProductionStatus;

public record VehicleProductionResponse(String vehicleId, String orderId, ProductionStage currentStage,
                                        ProductionStatus status, OffsetDateTime startedAt, OffsetDateTime completedAt) {
}