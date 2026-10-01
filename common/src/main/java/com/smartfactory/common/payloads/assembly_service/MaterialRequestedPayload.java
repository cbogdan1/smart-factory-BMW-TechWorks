package com.smartfactory.common.payloads.assembly_service;

import com.smartfactory.common.enums.ProductionStage;
import jakarta.validation.constraints.NotBlank;

public record MaterialRequestedPayload(@NotBlank String requestId, @NotBlank String vehicleId, @NotBlank String orderId,
                                       @NotBlank String material, @NotBlank int quantity,
                                       @NotBlank ProductionStage targetStage, @NotBlank String targetNode) {
}
