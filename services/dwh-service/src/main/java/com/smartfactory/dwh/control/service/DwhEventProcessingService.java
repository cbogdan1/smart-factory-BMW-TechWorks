package com.smartfactory.dwh.control.service;

import static com.smartfactory.common.exception.DataWarehouseErrorCodes.INVALID_EVENT;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DwhEventProcessingService {
    private final EventStoreService eventStoreService;
    private final VehicleTimelineService vehicleTimelineService;
    private final VehicleTwinService vehicleTwinService;

    public DwhEventProcessingService(final EventStoreService eventStoreService,
            final VehicleTimelineService vehicleTimelineService, final VehicleTwinService vehicleTwinService) {
        this.eventStoreService = eventStoreService;
        this.vehicleTimelineService = vehicleTimelineService;
        this.vehicleTwinService = vehicleTwinService;
    }

    public void processEvent(final DomainEvent<?> event) {
        if (event == null || event.eventId() == null || event.eventType() == null) {
            throw new BusinessException("Received invalid event", INVALID_EVENT, 400);
        }

        vehicleTimelineService.saveTimelineEntry(event);
        vehicleTwinService.processEvent(event);
        eventStoreService.saveEvent(event);
    }
}
