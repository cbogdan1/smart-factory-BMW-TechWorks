package com.smartfactory.common.dto.order;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.Priority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {

    private String orderId;

    private String vehicleId;

    private String customerName;

    private String customerEmail;

    private String vehicleModel;

    private String color;

    private BatteryType batteryType;

    private Priority priority;

    private OrderStatus status;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private LocalDateTime completionDate;
}
