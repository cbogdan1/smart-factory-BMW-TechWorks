package com.smartfactory.common.event;

import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;

public record QualityReworkRequiredEvent(DomainEvent<QualityReworkRequiredPayload> event) {
}
