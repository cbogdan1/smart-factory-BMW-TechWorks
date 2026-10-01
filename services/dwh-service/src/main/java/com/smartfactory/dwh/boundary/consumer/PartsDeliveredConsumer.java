package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.PARTS_DELIVERED;

import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PartsDeliveredConsumer {
    private static final Logger LOG = Logger.getLogger(PartsDeliveredConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public PartsDeliveredConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(PARTS_DELIVERED)
    public void consume(final PartsDeliveredEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", PARTS_DELIVERED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());
    }
}
