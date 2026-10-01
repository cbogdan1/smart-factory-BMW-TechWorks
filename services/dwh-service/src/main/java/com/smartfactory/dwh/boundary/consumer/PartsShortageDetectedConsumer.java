package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.PARTS_SHORTAGE_DETECTED;

import com.smartfactory.common.event.PartsShortageDetectedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PartsShortageDetectedConsumer {
    private static final Logger LOG = Logger.getLogger(PartsShortageDetectedConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public PartsShortageDetectedConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(PARTS_SHORTAGE_DETECTED)
    public void consume(final PartsShortageDetectedEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", PARTS_SHORTAGE_DETECTED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());
    }
}
