package com.smartfactory.common.event;

import com.smartfactory.common.payloads.quality_service.QualityFailedPayload;

public record QualityFailedEvent(DomainEvent<QualityFailedPayload> event) {
}
