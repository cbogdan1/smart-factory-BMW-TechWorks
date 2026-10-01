package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.QUALITY_FAILED;

import com.smartfactory.common.event.QualityFailedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class QualityFailedConsumer {
    private static final Logger LOG = Logger.getLogger(QualityFailedConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public QualityFailedConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(QUALITY_FAILED)
    public void consume(final QualityFailedEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", QUALITY_FAILED, event.event().eventId());
        
        dwhEventProcessingService.processEvent(event.event());
    }
}
