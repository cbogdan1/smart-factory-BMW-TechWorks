package com.smartfactory.common.event;

import com.smartfactory.common.payloads.planning_service.PlanningKpiUpdatedPayload;

public record PlanningKpiUpdatedEvent(
        DomainEvent<PlanningKpiUpdatedPayload> event) {
}