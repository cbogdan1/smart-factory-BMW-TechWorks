package com.smartfactory.common.payloads.planning_service;

import java.time.LocalDate;

import com.smartfactory.common.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductionPlannedPayload(@NotBlank String planId, @NotBlank String orderId, @NotBlank String vehicleId,
                                       @NotBlank String vehicleModel, @NotBlank String productionLine,
                                       @NotNull Priority priority, @NotNull LocalDate plannedStartDate) {
}