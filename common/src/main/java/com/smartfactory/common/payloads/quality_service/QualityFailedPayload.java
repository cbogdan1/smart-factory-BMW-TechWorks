package com.smartfactory.common.payloads.quality_service;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QualityFailedPayload(@NotBlank String inspectionId, @NotBlank String vehicleId, int score,
                                   @NotNull List<String> defectCodes, @NotNull Instant decidedAt) {
}
