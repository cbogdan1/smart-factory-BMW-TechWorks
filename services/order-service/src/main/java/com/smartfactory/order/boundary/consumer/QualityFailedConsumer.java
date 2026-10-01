package com.smartfactory.order.boundary.consumer;

import java.util.Optional;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.QualityFailedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.quality_service.QualityFailedPayload;
import com.smartfactory.order.control.order_service.OrderProgressService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Closes the failure loop: a vehicle scrapped by quality moves its order to the terminal FAILED state.
 */
@ApplicationScoped
public class QualityFailedConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(QualityFailedConsumer.class);

    @Inject
    OrderProgressService service;

    @Incoming(Topics.QUALITY_FAILED)
    public void onQualityFailed(final QualityFailedEvent wrapperEvent) {
        final DomainEvent<QualityFailedPayload> event =
                Optional.ofNullable(wrapperEvent).map(QualityFailedEvent::event).orElseThrow(
                        () -> new BusinessException("Received an empty quality-failed event", "ORDER_FAILURE_ERROR"));

        LOG.info("Processing {} eventId={} vehicleId={}", Topics.QUALITY_FAILED, event.eventId(),
                event.payload().vehicleId());

        service.failOrderFromQualityFailure(event.eventId(), event.payload());
    }
}

