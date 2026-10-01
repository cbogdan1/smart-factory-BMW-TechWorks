package com.smartfactory.dwh.control.service;

import static com.smartfactory.common.Topics.ORDER_COMPLETED;
import static com.smartfactory.common.Topics.ORDER_CREATED;
import static com.smartfactory.common.Topics.QUALITY_APPROVED;
import static com.smartfactory.common.Topics.QUALITY_FAILED;
import static com.smartfactory.common.Topics.QUALITY_REWORK_REQUIRED;
import static com.smartfactory.common.Topics.VEHICLE_ASSEMBLED;
import static com.smartfactory.common.exception.DataWarehouseErrorCodes.DASHBOARD_KPIS_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import com.smartfactory.common.dto.dwh.DashboardKpiResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.dwh.control.repository.EventStoreRepository;
import com.smartfactory.dwh.control.repository.VehicleTwinRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class KpiServiceTest {
    @Inject
    KpiService kpiService;

    @InjectMock
    EventStoreRepository eventStoreRepository;

    @InjectMock
    VehicleTwinRepository vehicleTwinRepository;

    @Test
    void shouldBuildDashboardKpis() {
        final Optional<Instant> timestamp = Optional.of(Instant.parse("2026-08-01T14:05:00Z"));
        when(eventStoreRepository.countEventsByType()).thenReturn(
                Map.of(ORDER_CREATED, 12L, VEHICLE_ASSEMBLED, 7L, ORDER_COMPLETED, 6L, QUALITY_APPROVED, 5L,
                        QUALITY_REWORK_REQUIRED, 2L, QUALITY_FAILED, 1L));
        when(vehicleTwinRepository.countVehiclesInProduction()).thenReturn(4L);
        when(eventStoreRepository.getLastUpdatedTimestamp()).thenReturn(timestamp);
        final DashboardKpiResponse result = kpiService.getDashboardKpis();

        assertEquals(12L, result.ordersCreated());
        assertEquals(4L, result.vehiclesInProduction());
        assertEquals(7L, result.vehiclesAssembled());
        assertEquals(6L, result.vehiclesCompleted());
        assertEquals((5D * 100D) / 7D, result.qualityPassRate(), 0.001);
        assertEquals(2L, result.reworkCount());
        assertEquals(1L, result.failedCount());
        assertEquals(timestamp, result.lastRefreshed());
    }

    @Test
    void shouldReturnZeroPassRateWhenNoVehiclesAssembled() {
        when(eventStoreRepository.countEventsByType()).thenReturn(Map.of(QUALITY_APPROVED, 5L));
        when(vehicleTwinRepository.countVehiclesInProduction()).thenReturn(0L);
        when(eventStoreRepository.getLastUpdatedTimestamp()).thenReturn(Optional.empty());

        final DashboardKpiResponse result = kpiService.getDashboardKpis();

        assertEquals(0.0, result.qualityPassRate());
    }

    @Test
    void shouldThrowBusinessExceptionWhenDashboardKpisCannotBeBuilt() {

        when(eventStoreRepository.countEventsByType()).thenThrow(new RuntimeException("DB error"));

        final BusinessException exception = assertThrows(BusinessException.class, () -> kpiService.getDashboardKpis());

        assertEquals("Failed to build dashboard KPIs", exception.getMessage());

        assertEquals(DASHBOARD_KPIS_FAILED, exception.getErrorCode());
        assertEquals(500, exception.getStatusCode());
    }
}
