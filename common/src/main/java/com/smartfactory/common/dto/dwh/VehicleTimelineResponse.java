package com.smartfactory.common.dto.dwh;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTimelineResponse {
    private String eventType;
    private String sourceService;
    private Instant eventTimestamp;
}