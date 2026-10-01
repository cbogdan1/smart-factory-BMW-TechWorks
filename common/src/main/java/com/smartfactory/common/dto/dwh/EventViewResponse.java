package com.smartfactory.common.dto.dwh;

import java.time.Instant;

public record EventViewResponse(String eventId, String eventType, String sourceService, Instant timestamp,
                                String correlationId) {

}