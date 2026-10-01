package com.smartfactory.common.dto.dwh;

import java.time.Instant;
import java.util.Optional;

public record DashboardKpiResponse(long ordersCreated, long vehiclesInProduction, long vehiclesAssembled,
                                   long vehiclesCompleted, double qualityPassRate, long reworkCount, long failedCount,
                                   Optional<Instant> lastRefreshed) {
}
