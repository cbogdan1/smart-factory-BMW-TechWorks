package com.smartfactory.common.dto.dwh;

import java.time.Instant;
import java.util.Optional;

public record AnalyticsSummaryResponse(long totalOrdersCreated, long totalPlansCreated, long totalPartsReserved,
                                       long totalVehiclesAssembled, long totalEventsStored,
                                       Optional<Instant> lastUpdated) {
}