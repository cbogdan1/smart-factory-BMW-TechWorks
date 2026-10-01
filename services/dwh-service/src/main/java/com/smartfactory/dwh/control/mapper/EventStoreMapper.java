package com.smartfactory.dwh.control.mapper;

import java.time.Instant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfactory.common.dto.dwh.EventViewResponse;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.dwh.entity.EventStoreEntity;
import io.quarkus.arc.Arc;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventStoreMapper {
    public static EventStoreEntity toEntity(final DomainEvent<?> event) {
        if (event == null) {
            return null;
        }

        String jsonPayload = null;
        if (event.payload() != null) {
            try {
                final ObjectMapper quarkusMapper = Arc.container().instance(ObjectMapper.class).get();
                jsonPayload = quarkusMapper.writeValueAsString(event.payload());
            } catch (final JsonProcessingException e) {
                throw new BusinessException("Failed to serialize event payload to JSON",
                        DataWarehouseErrorCodes.EVENT_SERIALIZATION_FAILED, 500);
            }
        }

        return EventStoreEntity.builder().eventId(event.eventId()).eventType(event.eventType())
                .correlationId(event.correlationId()).sourceService(event.sourceSystem()).timestamp(event.timestamp())
                .payload(jsonPayload).processedAt(Instant.now()).build();
    }

    public static EventViewResponse toView(final EventStoreEntity event) {
        if (event == null) {
            return null;
        }

        final EventViewResponse view =
                new EventViewResponse(event.getEventId(), event.getEventType(), event.getSourceService(),
                        event.getTimestamp(), event.getCorrelationId()

                );

        return view;
    }
}
