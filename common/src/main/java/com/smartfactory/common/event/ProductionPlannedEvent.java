package com.smartfactory.common.event;

import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;

public record ProductionPlannedEvent(DomainEvent<ProductionPlannedPayload> event) {

}