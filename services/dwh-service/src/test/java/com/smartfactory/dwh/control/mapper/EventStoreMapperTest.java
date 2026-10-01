package com.smartfactory.dwh.control.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import com.smartfactory.common.dto.dwh.EventViewResponse;
import com.smartfactory.dwh.entity.EventStoreEntity;
import org.junit.jupiter.api.Test;

class EventStoreMapperTest {
    @Test
    void shouldMapEntityToView() {
        final Instant timestamp = Instant.now();
        final EventStoreEntity entity = new EventStoreEntity();
        entity.setEventId("EVT-123");
        entity.setEventType("order-created");
        entity.setSourceService("order-service");
        entity.setCorrelationId("VEH-001");
        entity.setTimestamp(timestamp);

        final EventViewResponse view = EventStoreMapper.toView(entity);

        assertEquals("EVT-123", view.eventId());
        assertEquals("order-created", view.eventType());
        assertEquals("order-service", view.sourceService());
        assertEquals("VEH-001", view.correlationId());
        assertEquals(timestamp, view.timestamp());

    }
}