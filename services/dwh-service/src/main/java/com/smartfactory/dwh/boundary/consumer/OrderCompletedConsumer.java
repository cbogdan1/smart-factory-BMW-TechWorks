package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.ORDER_COMPLETED;

import com.smartfactory.common.event.OrderCompletedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderCompletedConsumer {
    private static final Logger LOG = Logger.getLogger(OrderCompletedConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public OrderCompletedConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(ORDER_COMPLETED)
    public void consume(final OrderCompletedEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", ORDER_COMPLETED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());
    }
}
