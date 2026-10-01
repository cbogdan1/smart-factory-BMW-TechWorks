package com.smartfactory.order.control.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.order.entity.Order;
import org.junit.jupiter.api.Test;

public class OrderCreatedPayloadMapperTest {

    @Test
    void shouldBuildPayloadFromOrder() {

        Order order = new Order();
        order.setOrderId("ORD-001");
        order.setVehicleId("VEH-001");
        order.setCustomerName("John");
        order.setCustomerEmail("john@test.com");
        order.setVehicleModel("BMW_I4");
        order.setColor("BLUE");
        order.setBatteryType(BatteryType.LONG_RANGE);
        order.setPriority(Priority.HIGH);
        order.setStatus(OrderStatus.CREATED);

        OrderCreatedPayloadMapper mapper = new OrderCreatedPayloadMapper();

        OrderCreatedPayload payload = mapper.toPayload(order);

        assertEquals("ORD-001", payload.orderId());
        assertEquals("VEH-001", payload.vehicleId());
        assertEquals("John", payload.customerName());
        assertEquals("john@test.com", payload.customerEmail());
        assertEquals("BMW_I4", payload.vehicleModel());
        assertEquals("BLUE", payload.color());
        assertEquals("LONG_RANGE", payload.batteryType());
        assertEquals(Priority.HIGH, payload.priority());
        assertEquals(OrderStatus.CREATED, payload.status());
    }
}
