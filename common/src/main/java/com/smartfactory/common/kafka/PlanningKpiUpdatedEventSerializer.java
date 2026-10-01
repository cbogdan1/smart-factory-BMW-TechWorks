package com.smartfactory.common.kafka;

import com.smartfactory.common.event.PlanningKpiUpdatedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class PlanningKpiUpdatedEventSerializer
        extends ObjectMapperSerializer<PlanningKpiUpdatedEvent> {
}