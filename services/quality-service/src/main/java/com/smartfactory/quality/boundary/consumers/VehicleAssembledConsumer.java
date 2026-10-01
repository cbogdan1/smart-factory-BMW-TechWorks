package com.smartfactory.quality.boundary.consumers;

import static com.smartfactory.common.Topics.VEHICLE_ASSEMBLED;
import static com.smartfactory.common.exception.QualityServiceExceptions.INVALID_VEHICLE_ASSEMBLED_EVENT_ERROR_CODE;
import static com.smartfactory.common.exception.QualityServiceExceptions.INVALID_VEHICLE_ASSEMBLED_EVENT_ERROR_MESSAGE;

import com.smartfactory.common.event.VehicleAssembledEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.quality.control.services.InspectionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VehicleAssembledConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleAssembledConsumer.class);

    @Inject
    InspectionService inspectionService;

    @Incoming(VEHICLE_ASSEMBLED)
    public void consume(final VehicleAssembledEvent event) {

        if (event == null || event.event() == null || event.event().payload() == null) {
            throw new BusinessException(INVALID_VEHICLE_ASSEMBLED_EVENT_ERROR_MESSAGE,
                    INVALID_VEHICLE_ASSEMBLED_EVENT_ERROR_CODE, 400);
        }

        LOG.info("Received {} [eventId={}, vehicleId={}]", VEHICLE_ASSEMBLED, event.event().eventId(),
                event.event().payload().vehicleId());

        inspectionService.performInspection(event.event().eventId(), event.event().payload());
    }
}