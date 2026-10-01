package com.smartfactory.dwh.control.service;

import static com.smartfactory.common.Topics.ORDER_CREATED;
import static com.smartfactory.common.Topics.PARTS_RESERVED;
import static com.smartfactory.common.Topics.PRODUCTION_PLANNED;
import static com.smartfactory.common.Topics.VEHICLE_ASSEMBLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import com.smartfactory.common.dto.dwh.AnalyticsSummaryResponse;
import com.smartfactory.dwh.control.repository.EventStoreRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AnalyticsServiceTest {

    @Inject
    AnalyticsService analyticsService;

    @InjectMock
    EventStoreRepository eventStoreRepository;

    @Test
    void shouldBuildAnalyticsSummary() {

        final Optional<Instant> timestamp = Optional.of(Instant.parse("2026-08-01T14:05:00Z"));
        when(eventStoreRepository.countEventsByType()).thenReturn(
                Map.of(ORDER_CREATED, 5L, PRODUCTION_PLANNED, 5L, PARTS_RESERVED, 12L, VEHICLE_ASSEMBLED, 3L));
        when(eventStoreRepository.count()).thenReturn(34L);
        when(eventStoreRepository.getLastUpdatedTimestamp()).thenReturn(timestamp);
        final AnalyticsSummaryResponse result = analyticsService.getAnalyticsSummary();

        assertEquals(5L, result.totalOrdersCreated());
        assertEquals(5L, result.totalPlansCreated());
        assertEquals(12L, result.totalPartsReserved());
        assertEquals(3L, result.totalVehiclesAssembled());
        assertEquals(25L, result.totalEventsStored());
        assertEquals(timestamp, result.lastUpdated());
    }

}