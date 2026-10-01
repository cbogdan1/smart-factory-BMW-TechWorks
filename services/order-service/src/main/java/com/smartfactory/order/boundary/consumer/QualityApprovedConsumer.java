package com.smartfactory.order.boundary.consumer;

import java.util.Optional;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.QualityApprovedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;
import com.smartfactory.order.control.order_service.OrderProgressService;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class QualityApprovedConsumer {
    private static final Logger log = LoggerFactory.getLogger(QualityApprovedConsumer.class);
    @Inject
    OrderProgressService service;

    @Incoming(Topics.QUALITY_APPROVED)
    public void onQualityApproved(@NotNull final QualityApprovedEvent wrapperEvent) {
        final DomainEvent<QualityApprovedPayload> event = Optional.ofNullable(wrapperEvent.event()).orElseThrow(
                () -> new BusinessException("Failed to complete order from quality approval",
                        "ORDER_COMPLETION_ERROR"));
        service.completeOrderFromQualityApproval(event.eventId(), event.payload());

    }

}
