package com.smartfactory.dwh.control.service;

import static com.smartfactory.common.Topics.ORDER_CREATED;
import static com.smartfactory.common.Topics.PARTS_RESERVED;
import static com.smartfactory.common.Topics.PRODUCTION_PLANNED;
import static com.smartfactory.common.Topics.VEHICLE_ASSEMBLED;
import static com.smartfactory.common.exception.DataWarehouseErrorCodes.ANALYTICS_SUMMARY_FAILED;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import com.smartfactory.common.dto.dwh.AnalyticsSummaryResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.dwh.control.repository.EventStoreRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AnalyticsService {
    private static final Logger LOG = Logger.getLogger(AnalyticsService.class);
    private final EventStoreRepository eventStoreRepository;

    public AnalyticsService(final EventStoreRepository eventStoreRepository) {
        this.eventStoreRepository = eventStoreRepository;
    }

    public AnalyticsSummaryResponse getAnalyticsSummary() {

        try {
            LOG.info("Building analytics summary");

            final Map<String, Long> eventCounts = eventStoreRepository.countEventsByType();

            final long totalOrdersCreated = eventCounts.getOrDefault(ORDER_CREATED, 0L);
            final long totalPlansCreated = eventCounts.getOrDefault(PRODUCTION_PLANNED, 0L);
            final long totalPartsReserved = eventCounts.getOrDefault(PARTS_RESERVED, 0L);
            final long totalVehiclesAssembled = eventCounts.getOrDefault(VEHICLE_ASSEMBLED, 0L);
            final long totalEventsStored = eventCounts.values().stream().mapToLong(Long::longValue).sum();
            final Optional<Instant> lastUpdated = eventStoreRepository.getLastUpdatedTimestamp();

            return new AnalyticsSummaryResponse(totalOrdersCreated, totalPlansCreated, totalPartsReserved,
                    totalVehiclesAssembled, totalEventsStored, lastUpdated);

        } catch (final Exception e) {
            LOG.error("Failed to build analytics summary", e);
            throw new BusinessException("Failed to build analytics summary", ANALYTICS_SUMMARY_FAILED, 500);
        }
    }

}
