package com.smartfactory.common.payloads.order_service;

import java.time.Instant;

import com.smartfactory.common.enums.OrderStatus;

public record OrderStatusUpdatedPayload (
   String orderId,
   String vehicleId,
   OrderStatus previousStatus,
   OrderStatus newStatus,
   Instant updatedAt
) {}
