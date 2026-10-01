package com.smartfactory.common.event;

import com.smartfactory.common.payloads.procurement.PartsShortageDetectedPayload;

public record PartsShortageDetectedEvent(DomainEvent<PartsShortageDetectedPayload> event) {
}
