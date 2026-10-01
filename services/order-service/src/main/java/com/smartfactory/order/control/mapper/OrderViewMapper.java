package com.smartfactory.order.control.mapper;

import com.smartfactory.common.dto.order.OrderViewResponse;
import com.smartfactory.order.entity.Order;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderViewMapper {

    public OrderViewResponse toResponse(final Order order) {
        return OrderViewResponse.builder().customerName(order.getCustomerName()).vehicleModel(order.getVehicleModel())
                .color(order.getColor()).batteryType(order.getBatteryType()).build();
    }
}
