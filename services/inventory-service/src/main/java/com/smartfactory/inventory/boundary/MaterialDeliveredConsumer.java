package com.smartfactory.inventory.boundary;

import static com.smartfactory.common.Topics.MATERIAL_DELIVERED;
import static com.smartfactory.common.exception.InventoryServiceExceptions.MATERIAL_DELIVERED_EVENT_INVALID_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.MATERIAL_DELIVERED_EVENT_INVALID_ERROR_MESSAGE;
import com.smartfactory.common.event.MaterialDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;
import com.smartfactory.inventory.control.ReservationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@Slf4j
public class MaterialDeliveredConsumer {

    @Inject
    ReservationService reservationService;

    @Incoming(MATERIAL_DELIVERED)
    public void consume(final MaterialDeliveredEvent event) {
        if (event == null || event.event() == null || event.event().payload() == null) {
            throw new BusinessException(
                    MATERIAL_DELIVERED_EVENT_INVALID_ERROR_MESSAGE,
                    MATERIAL_DELIVERED_EVENT_INVALID_ERROR_CODE,
                    400
            );
        }

        final MaterialDeliveredPayload payload = event.event().payload();
        log.info("Received {} event for missionId={}, vehicleId={}, material={}", MATERIAL_DELIVERED,
                payload.missionId(), payload.vehicleId(), payload.material());
        try {
            reservationService.processMaterialDelivered(payload);
        } catch (final Exception e) {
            log.error("Error processing MaterialDeliveredEvent for vehicleId={}", payload.vehicleId(), e);
        }
    }
}
