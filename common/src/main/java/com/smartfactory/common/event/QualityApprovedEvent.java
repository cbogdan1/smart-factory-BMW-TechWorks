package com.smartfactory.common.event;

import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;

public record QualityApprovedEvent(DomainEvent<QualityApprovedPayload> event) {
}
