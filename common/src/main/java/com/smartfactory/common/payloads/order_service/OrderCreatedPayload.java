package com.smartfactory.common.payloads.order_service;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderCreatedPayload(@NotBlank String orderId, @NotBlank String vehicleId, @NotBlank String customerName,
                                  @NotBlank String customerEmail, @NotBlank String vehicleModel, @NotBlank String color,
                                  @NotBlank String batteryType, @NotNull Priority priority,
                                  @NotNull OrderStatus status) {
}