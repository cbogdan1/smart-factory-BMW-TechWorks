package com.smartfactory.common.event;

import com.smartfactory.common.payloads.procurement.PartsDeliveredPayload;

public record PartsDeliveredEvent(DomainEvent<PartsDeliveredPayload> event) {
}
