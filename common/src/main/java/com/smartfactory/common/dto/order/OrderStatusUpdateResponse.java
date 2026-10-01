package com.smartfactory.common.dto.order;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.OrderStatus;
import lombok.Builder;

@Builder
public record OrderStatusUpdateResponse(OrderStatus currentStatus, LocalDateTime lastModifiedDate) {
}
