package com.smartfactory.dwh.control.mapper;

import java.time.Instant;
import java.util.List;

import com.smartfactory.common.dto.dwh.VehicleTimelineResponse;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.dwh.entity.VehicleTimelineEntity;
import lombok.experimental.UtilityClass;

/**
 * Utility mapper for VehicleTimeline
 */
@UtilityClass
public class VehicleTimelineMapper {

    public static VehicleTimelineResponse toResponse(final VehicleTimelineEntity entity) {
        if (entity == null) {
            return null;
        }

        return VehicleTimelineResponse.builder().eventType(entity.getEventType())
                .sourceService(entity.getSourceService()).eventTimestamp(entity.getEventTimestamp()).build();
    }

    public static List<VehicleTimelineResponse> toResponseList(final List<VehicleTimelineEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream().map(VehicleTimelineMapper::toResponse).toList();
    }

    public VehicleTimelineEntity toEntity(final DomainEvent<?> event) {
        if (event == null) {
            return null;
        }

        return VehicleTimelineEntity.builder().vehicleId(event.correlationId()).eventId(event.eventId())
                .eventType(event.eventType()).sourceService(event.sourceSystem()).eventTimestamp(event.timestamp())
                .createdAt(Instant.now()).build();
    }
}
