package com.smartfactory.inventory.boundary;

import static com.smartfactory.common.Topics.PARTS_DELIVERED;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.InventoryServiceExceptions;
import com.smartfactory.common.payloads.procurement.PartsDeliveredPayload;
import com.smartfactory.inventory.control.InventoryStockService;
import com.smartfactory.inventory.entity.InventoryStock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@Slf4j
public class PartsDeliveredConsumer {

    @Inject
    InventoryStockService inventoryStockService;

    @Incoming(PARTS_DELIVERED)
    public void consumePartsDelivered(final PartsDeliveredEvent event) {
        if (event == null || event.event() == null || event.event().eventId() == null || event.event().payload() == null) {
            throw new BusinessException(InventoryServiceExceptions.PARTS_DELIVERED_EVENT_INVALID_ERROR_MESSAGE,
                    InventoryServiceExceptions.PARTS_DELIVERED_EVENT_INVALID_ERROR_CODE, 400);
        }

        final DomainEvent<PartsDeliveredPayload> domainEvent = event.event();
        final String eventId = domainEvent.eventId();

        final InventoryStock updatedStock = inventoryStockService.processPartsDelivered(eventId, domainEvent.payload());
        inventoryStockService.checkAndPublishLowStock(updatedStock);
    }
}
