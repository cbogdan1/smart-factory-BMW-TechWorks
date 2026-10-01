package com.smartfactory.common.payloads.planning_service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record PlanningKpiUpdatedPayload(@NotBlank String productionLine, @PositiveOrZero int currentLoad,
                                        @PositiveOrZero int maximumCapacity) {
}