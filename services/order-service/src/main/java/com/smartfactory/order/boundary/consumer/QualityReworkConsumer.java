package com.smartfactory.order.boundary.consumer;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.QualityReworkRequiredEvent;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import com.smartfactory.order.control.order_service.OrderProgressService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class QualityReworkConsumer {
    @Inject
    OrderProgressService service;

    @Incoming(Topics.QUALITY_REWORK_REQUIRED)
    public void onQualityReworkRequired(final QualityReworkRequiredEvent wrapperEvent) {
        final DomainEvent<QualityReworkRequiredPayload> event = wrapperEvent.event();
        service.markOrderForRework(event.eventId(), event.payload());
    }
}