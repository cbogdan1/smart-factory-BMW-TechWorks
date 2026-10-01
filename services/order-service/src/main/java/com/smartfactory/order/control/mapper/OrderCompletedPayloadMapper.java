package com.smartfactory.order.control.mapper;

import java.util.Optional;

import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.order_service.OrderCompletedPayload;
import com.smartfactory.order.entity.Order;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderCompletedPayloadMapper {
    public OrderCompletedPayload toPayload(final Order order) {
        return Optional.ofNullable(order)
                .map(fromOrder -> OrderCompletedPayload.builder().orderId(fromOrder.getOrderId())
                        .vehicleId(fromOrder.getVehicleId()).status(fromOrder.getStatus()).build()).orElseThrow(
                        () -> new BusinessException("Cannot map null order to Kafka payload", "NULL_ORDER_PAYLOAD",
                                400));

    }
}
