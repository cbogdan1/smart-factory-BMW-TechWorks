package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.PRODUCTION_PLANNED;

import com.smartfactory.common.event.ProductionPlannedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductionPlannedConsumer {
    private static final Logger LOG = Logger.getLogger(ProductionPlannedConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public ProductionPlannedConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(PRODUCTION_PLANNED)
    public void consume(final ProductionPlannedEvent event) {

        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", PRODUCTION_PLANNED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());

    }
}