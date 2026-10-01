package com.smartfactory.order.boundary.consumer;

import java.util.Optional;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.VehicleAssembledEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.order.control.order_service.OrderProgressService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Moves the order into QUALITY_CHECK as soon as assembly reports the vehicle as assembled.
 *
 * <p>Without this the order would still be CREATED when the quality decision arrives, and every
 * quality-driven transition (COMPLETED / REWORK_REQUIRED / FAILED) would be rejected by the
 * order status state machine.
 */
@ApplicationScoped
public class VehicleAssembledConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleAssembledConsumer.class);

    @Inject
    OrderProgressService service;

    @Incoming(Topics.VEHICLE_ASSEMBLED)
    public void onVehicleAssembled(final VehicleAssembledEvent wrapperEvent) {
        final DomainEvent<VehicleAssembledPayload> event =
                Optional.ofNullable(wrapperEvent).map(VehicleAssembledEvent::event).orElseThrow(
                        () -> new BusinessException("Received an empty vehicle-assembled event",
                                "ORDER_QUALITY_CHECK_ERROR"));

        LOG.info("Processing {} eventId={} vehicleId={}", Topics.VEHICLE_ASSEMBLED, event.eventId(),
                event.payload().vehicleId());

        service.markOrderForQualityCheck(event.eventId(), event.payload());
    }
}

