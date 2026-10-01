package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.MATERIAL_DELIVERED;

import com.smartfactory.common.event.MaterialDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MaterialDeliveredConsumer {
    private static final Logger LOG = Logger.getLogger(MaterialDeliveredConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public MaterialDeliveredConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(MATERIAL_DELIVERED)
    public void consume(final MaterialDeliveredEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", MATERIAL_DELIVERED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());
    }
}
