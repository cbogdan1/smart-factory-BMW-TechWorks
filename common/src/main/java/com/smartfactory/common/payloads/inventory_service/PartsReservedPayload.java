package com.smartfactory.common.payloads.inventory_service;

import java.util.List;

import com.smartfactory.common.dto.inventory.ReservedPart;

public record PartsReservedPayload(String reservationId, String vehicleId, String planId,
                                   List<ReservedPart> reservedParts, String reservedAt) {
}