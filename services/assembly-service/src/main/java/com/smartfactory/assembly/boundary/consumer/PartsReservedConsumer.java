package com.smartfactory.assembly.boundary.consumer;

import static com.smartfactory.common.Topics.PARTS_RESERVED;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_PARTS_RESERVED_EVENT_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_PARTS_RESERVED_EVENT_ERROR_MESSAGE;

import com.smartfactory.assembly.control.services.VehicleProductionService;
import com.smartfactory.common.event.PartsReservedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.inventory_service.PartsReservedPayload;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PartsReservedConsumer {

    private static final Logger LOG = Logger.getLogger(PartsReservedConsumer.class);

    private final VehicleProductionService vehicleProductionService;

    public PartsReservedConsumer(final VehicleProductionService vehicleProductionService) {
        this.vehicleProductionService = vehicleProductionService;
    }

    @Incoming(PARTS_RESERVED)
    public void consume(final PartsReservedEvent partsReservedEvent) {
        if (partsReservedEvent == null
                || partsReservedEvent.event() == null
                || partsReservedEvent.event().payload() == null
                || partsReservedEvent.event().eventId() == null
                || partsReservedEvent.event().payload().vehicleId() == null) {
            throw new BusinessException(NULL_PARTS_RESERVED_EVENT_ERROR_MESSAGE, NULL_PARTS_RESERVED_EVENT_ERROR_CODE, 400);
        }

        final String eventId = partsReservedEvent.event().eventId();
        final PartsReservedPayload payload = partsReservedEvent.event().payload();

        LOG.infof(
                "Processing %s eventId=%s vehicleId=%s",
                PARTS_RESERVED,
                eventId,
                payload.vehicleId());

        vehicleProductionService.startProduction(eventId, payload);
    }
}
