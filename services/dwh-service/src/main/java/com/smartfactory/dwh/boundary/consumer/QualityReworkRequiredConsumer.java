package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.QUALITY_REWORK_REQUIRED;

import com.smartfactory.common.event.QualityReworkRequiredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class QualityReworkRequiredConsumer {
    private static final Logger LOG = Logger.getLogger(QualityReworkRequiredConsumer.class);

    private final DwhEventProcessingService dwhEventProcessingService;

    public QualityReworkRequiredConsumer(final DwhEventProcessingService dwhEventProcessingService) {
        this.dwhEventProcessingService = dwhEventProcessingService;
    }

    @Incoming(QUALITY_REWORK_REQUIRED)
    public void consume(final QualityReworkRequiredEvent event) {
        if (event == null) {
            LOG.warn("Received invalid event");
            throw new BusinessException("Received invalid event", DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        LOG.infof("Received %s eventId = %s", QUALITY_REWORK_REQUIRED, event.event().eventId());

        dwhEventProcessingService.processEvent(event.event());
    }
}
