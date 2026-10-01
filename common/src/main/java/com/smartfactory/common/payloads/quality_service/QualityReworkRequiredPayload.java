package com.smartfactory.common.payloads.quality_service;

import java.time.Instant;

import com.smartfactory.common.enums.ProductionStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QualityReworkRequiredPayload(@NotBlank String inspectionId, @NotBlank String vehicleId, int score,
                                           ProductionStage targetStage, @NotBlank String reason,
                                           @NotNull Instant decidedAt) {
}
