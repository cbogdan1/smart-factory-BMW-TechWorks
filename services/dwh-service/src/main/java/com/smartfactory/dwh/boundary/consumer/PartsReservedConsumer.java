package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.PARTS_RESERVED;

import com.smartfactory.common.event.PartsReservedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PartsReservedConsumer {
    private static final Logger LOG = Logger.getLogger(PartsReservedConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public PartsReservedConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(PARTS_RESERVED)
    public void consume(final PartsReservedEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", PARTS_RESERVED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());
    }
}

