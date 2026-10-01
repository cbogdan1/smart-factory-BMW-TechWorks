package com.smartfactory.order.control.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.payloads.order_service.OrderStatusUpdatedPayload;
import com.smartfactory.order.entity.Order;
import org.junit.jupiter.api.Test;

class OrderStatusUpdatedPayloadMapperTest {

    @Test
    void shouldBuildOrderStatusUpdatedPayload() {

        Order order = new Order();

        order.setOrderId("ORD-001");
        order.setVehicleId("VEH-001");
        order.setStatus(OrderStatus.PLANNED);

        OrderStatusUpdatedPayload payload = OrderStatusUpdatedPayloadMapper.build(order, OrderStatus.CREATED);

        assertNotNull(payload);

        assertEquals("ORD-001", payload.orderId());
        assertEquals("VEH-001", payload.vehicleId());
        assertEquals(OrderStatus.CREATED, payload.previousStatus());
        assertEquals(OrderStatus.PLANNED, payload.newStatus());
        assertNotNull(payload.updatedAt());
    }
}