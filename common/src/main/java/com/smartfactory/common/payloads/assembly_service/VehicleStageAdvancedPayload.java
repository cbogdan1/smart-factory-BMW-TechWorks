package com.smartfactory.common.payloads.assembly_service;

import java.time.Instant;

import com.smartfactory.common.enums.ProductionStage;
import jakarta.validation.constraints.NotBlank;

public record VehicleStageAdvancedPayload(@NotBlank String vehicleId, @NotBlank String orderId,
                                          @NotBlank ProductionStage previousStage,
                                          @NotBlank ProductionStage currentStage, @NotBlank Instant advancedAt) {
}
