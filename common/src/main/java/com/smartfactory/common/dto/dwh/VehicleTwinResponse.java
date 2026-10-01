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
public class VehicleTwinResponse {
    private String vehicleId;
    private String orderId;
    private String vehicleModel;
    private String status;
    private String currentStage;
    private String currentLocation;
    private String qualityStatus;
    private Integer reworkCount;
    private Instant lastUpdated;
}
