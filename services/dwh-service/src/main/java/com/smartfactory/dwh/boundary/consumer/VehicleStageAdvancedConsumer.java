package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.VEHICLE_STAGE_ADVANCED;

import com.smartfactory.common.event.VehicleStageAdvancedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class VehicleStageAdvancedConsumer {
    private static final Logger LOG = Logger.getLogger(VehicleStageAdvancedConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public VehicleStageAdvancedConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(VEHICLE_STAGE_ADVANCED)
    public void consume(final VehicleStageAdvancedEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", VEHICLE_STAGE_ADVANCED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());
    }
}
