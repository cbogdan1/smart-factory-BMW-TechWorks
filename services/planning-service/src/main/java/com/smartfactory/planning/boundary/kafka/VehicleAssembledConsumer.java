package com.smartfactory.planning.boundary.kafka;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.VehicleAssembledEvent;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.planning.control.service.ProductionPlanService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class VehicleAssembledConsumer {
    @Inject
    ProductionPlanService productionPlanService;

    @Incoming(Topics.VEHICLE_ASSEMBLED)
    public void consume(final VehicleAssembledEvent vehicleAssembledEvent) {
        final DomainEvent<VehicleAssembledPayload> event = vehicleAssembledEvent.event();

        productionPlanService.completePlan(event.eventId(), event.payload());
    }
}