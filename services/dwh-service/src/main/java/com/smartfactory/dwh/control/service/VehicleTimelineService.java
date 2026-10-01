package com.smartfactory.dwh.control.service;

import static com.smartfactory.common.exception.DataWarehouseErrorCodes.INVALID_VEHICLE_ID;
import static com.smartfactory.common.exception.DataWarehouseErrorCodes.TIMELINE_NOT_FOUND;

import java.util.List;

import com.smartfactory.common.dto.dwh.VehicleTimelineResponse;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.dwh.control.mapper.VehicleTimelineMapper;
import com.smartfactory.dwh.control.repository.VehicleTimelineRepository;
import com.smartfactory.dwh.entity.VehicleTimelineEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/**
 * Service handling business logic and persistence for vehicle timeline records.
 * Ensures idempotent event saving and constructs chronologically ordered timelines.
 */
@ApplicationScoped
public class VehicleTimelineService {

    private static final Logger LOG = Logger.getLogger(VehicleTimelineService.class);

    private final VehicleTimelineRepository vehicleTimelineRepository;

    public VehicleTimelineService(final VehicleTimelineRepository vehicleTimelineRepository) {
        this.vehicleTimelineRepository = vehicleTimelineRepository;
    }

    /**
     * Retrieves the chronological event timeline for a specified vehicle.
     *
     * @param vehicleId the unique id of the vehicle
     * @return a list of mapped {@link VehicleTimelineResponse} DTOs
     */
    public List<VehicleTimelineResponse> getVehicleTimeline(final String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new BusinessException("Invalid vehicleId supplied", INVALID_VEHICLE_ID, 400);
        }

        LOG.infof("Fetching vehicle timeline for vehicleId = %s", vehicleId);
        final List<VehicleTimelineEntity> events = vehicleTimelineRepository.findByVehicleId(vehicleId);

        if (events.isEmpty()) {
            LOG.warnf("No timeline events found for vehicleId = %s", vehicleId);
            throw new BusinessException("Timeline entries not found for " + vehicleId, TIMELINE_NOT_FOUND, 404);
        }

        LOG.infof("Successfully fetched %d timeline events for vehicleId = %s", events.size(), vehicleId);

        return VehicleTimelineMapper.toResponseList(events);
    }

    /**
     * Persists an incoming domain event to the vehicle timeline if it does not already exist.
     *
     * @param event the event payload received from Kafka consumers
     */
    @Transactional
    public void saveTimelineEntry(final DomainEvent<?> event) {
        if (event == null || event.eventId() == null) {
            LOG.warn("[TIMELINE] Received null event or missing eventId");
            return;
        }

        final String eventId = event.eventId();
        final String eventType = event.eventType();
        final String vehicleId = event.correlationId();

        LOG.infof("[%s] Processing timeline entry for vehicleId=%s, eventId=%s", eventType, vehicleId, eventId);

        if (vehicleTimelineRepository.existsByEventId(eventId)) {
            LOG.infof("[%s] Timeline entry already exists for eventId=%s (skipping)", eventType, eventId);
            return;
        }

        final VehicleTimelineEntity entity = VehicleTimelineMapper.toEntity(event);
        vehicleTimelineRepository.persist(entity);

        LOG.infof("[%s] Successfully persisted timeline entry for vehicleId=%s, eventId=%s", eventType, vehicleId,
                eventId);
    }
}
