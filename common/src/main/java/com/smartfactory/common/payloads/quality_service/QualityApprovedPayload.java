package com.smartfactory.common.payloads.quality_service;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QualityApprovedPayload(@NotBlank String inspectionId, @NotBlank String vehicleId, int score,
                                     @NotNull Instant decidedAt) {
}
