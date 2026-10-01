package com.smartfactory.assembly.boundary.consumer;

import static com.smartfactory.common.Topics.PRODUCTION_PLANNED;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_PRODUCTION_PLANNED_EVENT_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_PRODUCTION_PLANNED_EVENT_ERROR_MESSAGE;

import com.smartfactory.assembly.control.services.VehicleProductionService;
import com.smartfactory.common.dto.assembly.CreateVehicleProductionRequest;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.ProductionPlannedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ProductionPlannedEventConsumer {

    @Inject
    VehicleProductionService service;

    @Incoming(PRODUCTION_PLANNED)
    @Blocking
    public void consume(final ProductionPlannedEvent productionPlannedEvent) {
        if (productionPlannedEvent == null || productionPlannedEvent.event() == null
                || productionPlannedEvent.event().payload() == null) {
            throw new BusinessException(NULL_PRODUCTION_PLANNED_EVENT_ERROR_MESSAGE, NULL_PRODUCTION_PLANNED_EVENT_ERROR_CODE, 400);
        }

        final DomainEvent<ProductionPlannedPayload> event = productionPlannedEvent.event();

        final ProductionPlannedPayload payload = event.payload();

        final CreateVehicleProductionRequest request =
                new CreateVehicleProductionRequest(payload.vehicleId(), payload.orderId(), payload.vehicleModel(),
                        payload.productionLine());

        service.create(request);
    }
}
