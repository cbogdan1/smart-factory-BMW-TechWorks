package com.smartfactory.agv.boundary.kafka;

import com.smartfactory.agv.control.service.DeliveryMissionService;
import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.MaterialRequestedEvent;
import com.smartfactory.common.payloads.assembly_service.MaterialRequestedPayload;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class MaterialRequestedConsumer {

    @Inject
    DeliveryMissionService deliveryMissionService;

    @Incoming(Topics.MATERIAL_REQUESTED)
    @Blocking
    public void consumeMaterialRequest(final MaterialRequestedEvent message) {
        if (message == null || message.event() == null || message.event().payload() == null) {
            throw new WebApplicationException(
                    Response.status(400).entity("Material-requested event must not be null").build());
        }

        final DomainEvent<MaterialRequestedPayload> event = message.event();

        deliveryMissionService.executeDeliveryMission(event.eventId(), event.eventType(), event.payload());
    }
}