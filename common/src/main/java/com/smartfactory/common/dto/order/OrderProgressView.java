package com.smartfactory.common.dto.order;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.ProductionStage;

public record OrderProgressView(
        String orderId,
        String vehicleId,
        OrderStatus status,
        ProductionStage currentFactoryStage,
        LocalDateTime completionDate,
        LocalDateTime lastUpdated
) { }