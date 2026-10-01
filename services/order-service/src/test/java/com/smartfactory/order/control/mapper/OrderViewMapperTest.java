package com.smartfactory.order.control.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.smartfactory.common.dto.order.OrderViewResponse;
import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.order.entity.Order;
import org.junit.jupiter.api.Test;

public class OrderViewMapperTest {

    @Test
    void shouldMapOrderViewResponse() {

        Order order = new Order();
        order.setCustomerName("John");
        order.setVehicleModel("BMW_I4");
        order.setColor("BLUE");
        order.setBatteryType(BatteryType.LONG_RANGE);

        OrderViewMapper mapper = new OrderViewMapper();

        OrderViewResponse response = mapper.toResponse(order);

        assertNotNull(response);
        assertEquals("John", response.customerName());
        assertEquals("BMW_I4", response.vehicleModel());
        assertEquals("BLUE", response.color());
        assertEquals(BatteryType.LONG_RANGE, response.batteryType());
    }
}
