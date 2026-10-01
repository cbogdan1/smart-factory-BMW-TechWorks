package com.smartfactory.dwh.control.service;

import static com.smartfactory.common.exception.DataWarehouseErrorCodes.INVALID_VEHICLE_ID;
import static com.smartfactory.common.exception.DataWarehouseErrorCodes.VEHICLE_NOT_FOUND;

import com.smartfactory.common.dto.dwh.VehicleTwinResponse;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.dwh.control.mapper.VehicleTwinMapper;
import com.smartfactory.dwh.control.repository.EventStoreRepository;
import com.smartfactory.dwh.control.repository.VehicleTwinRepository;
import com.smartfactory.dwh.control.strategy.DigitalTwinUpdateStrategy;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class VehicleTwinService {

    private static final Logger LOG = Logger.getLogger(VehicleTimelineService.class);

    private final VehicleTwinRepository vehicleTwinRepository;
    private final EventStoreRepository eventStoreRepository;
    private final Instance<DigitalTwinUpdateStrategy> strategies;

    public VehicleTwinService(final VehicleTwinRepository vehicleTwinRepository,
            final EventStoreRepository eventStoreRepository, final Instance<DigitalTwinUpdateStrategy> strategies) {
        this.vehicleTwinRepository = vehicleTwinRepository;
        this.eventStoreRepository = eventStoreRepository;
        this.strategies = strategies;
    }

    @Transactional
    public void processEvent(final DomainEvent<?> event) {
        if (event == null || event.eventId() == null) {
            LOG.warn("[VEHICLE_TWIN] Received null event or missing eventId");
            return;
        }

        final String eventId = event.eventId();
        final String eventType = event.eventType();
        final String vehicleId = event.correlationId();

        LOG.infof("[%s] Processing vehicle twin update for vehicleId=%s, eventId=%s", eventType, vehicleId, eventId);

        // Deduplication check expected by shouldSkipDuplicateEvent unit test
        if (eventStoreRepository.existsByEventId(eventId)) {
            LOG.infof("[%s] Event with eventId=%s already processed (skipping twin update)", eventType, eventId);
            return;
        }

        if (vehicleId == null || vehicleId.isBlank()) {
            LOG.warnf("[%s] Missing correlationId (vehicleId) for eventId=%s", eventType, eventId);
            return;
        }

        // Select the correct strategy
        final DigitalTwinUpdateStrategy strategy =
                strategies.stream().filter(s -> s.supports(eventType)).findFirst().orElse(null);

        if (strategy == null) {
            LOG.debugf("[%s] No twin strategy found for eventId=%s (skipping twin update)", eventType, eventId);
            return;
        }

        // Find existing or initialize new
        final VehicleTwinEntity existingTwin = vehicleTwinRepository.findByVehicleId(vehicleId);
        final VehicleTwinEntity twin = (existingTwin != null) ?
                existingTwin :
                VehicleTwinEntity.builder().vehicleId(vehicleId).reworkCount(0).build();

        // Apply strategy & persist
        strategy.update(twin, event);
        vehicleTwinRepository.persist(twin);

        LOG.infof("[%s] Successfully updated VehicleTwin for vehicleId=%s, eventId=%s", eventType, vehicleId, eventId);
    }

    public VehicleTwinResponse getVehicleTwin(final String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new BusinessException("Invalid vehicleId supplied", INVALID_VEHICLE_ID, 400);
        }

        LOG.infof("Fetching vehicle twin state for vehicleId = %s", vehicleId);

        final VehicleTwinEntity entity = vehicleTwinRepository.findByVehicleId(vehicleId);

        if (entity == null) {
            throw new BusinessException("Vehicle twin not found for " + vehicleId, VEHICLE_NOT_FOUND, 404);
        }

        return VehicleTwinMapper.toResponse(entity);
    }
}
