package com.smartfactory.dwh.control.service;

import static com.smartfactory.common.Topics.ORDER_COMPLETED;
import static com.smartfactory.common.Topics.ORDER_CREATED;
import static com.smartfactory.common.Topics.QUALITY_APPROVED;
import static com.smartfactory.common.Topics.QUALITY_FAILED;
import static com.smartfactory.common.Topics.QUALITY_REWORK_REQUIRED;
import static com.smartfactory.common.Topics.VEHICLE_ASSEMBLED;
import static com.smartfactory.common.exception.DataWarehouseErrorCodes.DASHBOARD_KPIS_FAILED;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import com.smartfactory.common.dto.dwh.DashboardKpiResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.dwh.control.repository.EventStoreRepository;
import com.smartfactory.dwh.control.repository.VehicleTwinRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KpiService {
    private static final Logger LOG = Logger.getLogger(KpiService.class);
    private final EventStoreRepository eventStoreRepository;
    private final VehicleTwinRepository vehicleTwinRepository;

    public KpiService(final EventStoreRepository eventStoreRepository,
            final VehicleTwinRepository vehicleTwinRepository) {
        this.eventStoreRepository = eventStoreRepository;
        this.vehicleTwinRepository = vehicleTwinRepository;
    }

    public DashboardKpiResponse getDashboardKpis() {

        try {
            LOG.info("Building dashboard KPIs");

            final Map<String, Long> eventCounts = eventStoreRepository.countEventsByType();

            final long ordersCreated = eventCounts.getOrDefault(ORDER_CREATED, 0L);
            final long vehiclesInProduction = vehicleTwinRepository.countVehiclesInProduction();
            final long vehiclesAssembled = eventCounts.getOrDefault(VEHICLE_ASSEMBLED, 0L);
            final long vehiclesCompleted = eventCounts.getOrDefault(ORDER_COMPLETED, 0L);
            final double qualityPassRate = vehiclesAssembled == 0 ?
                    0.0 :
                    (eventCounts.getOrDefault(QUALITY_APPROVED, 0L) * 100.0) / vehiclesAssembled;
            final long reworkCount = eventCounts.getOrDefault(QUALITY_REWORK_REQUIRED, 0L);
            final long failedCount = eventCounts.getOrDefault(QUALITY_FAILED, 0L);
            final Optional<Instant> lastUpdated = eventStoreRepository.getLastUpdatedTimestamp();

            return new DashboardKpiResponse(ordersCreated, vehiclesInProduction, vehiclesAssembled, vehiclesCompleted,
                    qualityPassRate, reworkCount, failedCount, lastUpdated);

        } catch (final Exception e) {
            LOG.error("Failed to build dashboard KPIs", e);
            throw new BusinessException("Failed to build dashboard KPIs", DASHBOARD_KPIS_FAILED, 500);
        }
    }
}
