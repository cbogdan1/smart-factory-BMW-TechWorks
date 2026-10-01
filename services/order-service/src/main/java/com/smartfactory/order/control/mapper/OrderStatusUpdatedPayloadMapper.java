package com.smartfactory.order.control.mapper;

import java.time.Instant;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.payloads.order_service.OrderStatusUpdatedPayload;
import com.smartfactory.order.entity.Order;

public class OrderStatusUpdatedPayloadMapper {

    public static OrderStatusUpdatedPayload build(final Order order, final OrderStatus previousStatus) {

        return new OrderStatusUpdatedPayload(order.getOrderId(), order.getVehicleId(), previousStatus,
                order.getStatus(), Instant.now());
    }
}
