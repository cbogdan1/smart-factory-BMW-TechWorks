package com.smartfactory.common.payloads.agv_service;

import java.time.Instant;

public record MaterialDeliveredPayload(String missionId, String agvId, String vehicleId, String material, int quantity,
                                       String targetNode, Instant deliveredAt) {
}