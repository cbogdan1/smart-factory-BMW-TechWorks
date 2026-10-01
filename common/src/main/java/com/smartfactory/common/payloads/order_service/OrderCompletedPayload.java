package com.smartfactory.common.payloads.order_service;

import com.smartfactory.common.enums.OrderStatus;
import lombok.Builder;

@Builder
public record OrderCompletedPayload(String orderId, String vehicleId, OrderStatus status) {
}
