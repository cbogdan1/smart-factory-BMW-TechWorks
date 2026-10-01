package com.smartfactory.common.event;

import com.smartfactory.common.payloads.inventory_service.PartsReservedPayload;

public record PartsReservedEvent(
        DomainEvent<PartsReservedPayload> event
) {}