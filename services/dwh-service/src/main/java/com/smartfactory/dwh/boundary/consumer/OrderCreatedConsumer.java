package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.ORDER_CREATED;

import com.smartfactory.common.event.OrderCreatedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderCreatedConsumer {
    private static final Logger LOG = Logger.getLogger(OrderCreatedConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public OrderCreatedConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(ORDER_CREATED)
    public void consume(final OrderCreatedEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", ORDER_CREATED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());
    }
}