package com.smartfactory.order.control.mapper;

import java.util.Optional;

import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.order.entity.Order;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderCreatedPayloadMapper {

    public OrderCreatedPayload toPayload(final Order order) {
        return Optional.ofNullable(order).map(fromOrder -> OrderCreatedPayload.builder().orderId(fromOrder.getOrderId())
                .vehicleId(fromOrder.getVehicleId()).customerName(fromOrder.getCustomerName())
                .customerEmail(fromOrder.getCustomerEmail()).vehicleModel(fromOrder.getVehicleModel())
                .color(fromOrder.getColor())
                .batteryType(fromOrder.getBatteryType() != null ? fromOrder.getBatteryType().name() : null)
                .priority(fromOrder.getPriority()).status(fromOrder.getStatus()).build()).orElseThrow(
                () -> new BusinessException("Cannot map null order to Kafka payload", "NULL_ORDER_PAYLOAD", 400));

    }

}