package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.VEHICLE_ASSEMBLED;

import com.smartfactory.common.event.VehicleAssembledEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class VehicleAssembledConsumer {
    private static final Logger LOG = Logger.getLogger(VehicleAssembledConsumer.class);
    private final DwhEventProcessingService dwhEventProcessingService;

    public VehicleAssembledConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(VEHICLE_ASSEMBLED)
    public void consume(final VehicleAssembledEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", VEHICLE_ASSEMBLED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());
    }
}
