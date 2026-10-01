package com.smartfactory.order.control.order_service;

import com.smartfactory.common.dto.order.CreateOrderRequest;
import com.smartfactory.common.dto.order.OrderResponse;
import com.smartfactory.common.dto.order.OrderStatusUpdateResponse;
import com.smartfactory.common.dto.order.OrderViewResponse;
import com.smartfactory.common.enums.OrderStatus;

public interface OrderService {
    OrderResponse createOrder(final CreateOrderRequest request);

    OrderViewResponse getOrderById(final String id);

    OrderStatusUpdateResponse changeStatus(final String orderId, final OrderStatus newStatus);
}
