package com.smartfactory.assembly.boundary.consumer;

import static com.smartfactory.common.Topics.QUALITY_REWORK_REQUIRED;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_MESSAGE;

import com.smartfactory.assembly.control.services.VehicleProductionReworkService;
import com.smartfactory.common.event.QualityReworkRequiredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class QualityReworkConsumer {

    private static final Logger LOG = Logger.getLogger(QualityReworkConsumer.class);

    private final VehicleProductionReworkService reworkService;

    public QualityReworkConsumer(final VehicleProductionReworkService reworkService) {
        this.reworkService = reworkService;
    }

    @Incoming(QUALITY_REWORK_REQUIRED)
    public void consume(final QualityReworkRequiredEvent qualityReworkEvent) {
        if (qualityReworkEvent == null
                || qualityReworkEvent.event() == null
                || qualityReworkEvent.event().payload() == null
                || qualityReworkEvent.event().eventId() == null
                || qualityReworkEvent.event().payload().vehicleId() == null
                || qualityReworkEvent.event().payload().targetStage() == null) {
            throw new BusinessException(
                    NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_MESSAGE,
                    NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_CODE,
                    400);
        }

        final String eventId = qualityReworkEvent.event().eventId();
        final QualityReworkRequiredPayload payload = qualityReworkEvent.event().payload();

        LOG.infof(
                "Processing %s eventId=%s vehicleId=%s targetStage=%s",
                QUALITY_REWORK_REQUIRED,
                eventId,
                payload.vehicleId(),
                payload.targetStage());

        reworkService.reenterRework(eventId, payload);
    }
}
