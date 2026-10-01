package com.smartfactory.order.control.mapper;

import java.time.LocalDateTime;
import java.util.Optional;

import com.smartfactory.common.dto.order.OrderStatusUpdateResponse;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderStatusResponseMapper {

    public OrderStatusResponseMapper() {

    }

    public OrderStatusUpdateResponse toResponse(final OrderStatus status) {
        return Optional.ofNullable(status).map(fromStatus -> OrderStatusUpdateResponse.builder().currentStatus(status)
                        .lastModifiedDate(LocalDateTime.now()).build())
                .orElseThrow(() -> new BusinessException("Cannot map to null status", "NULL_ORDER_STATUS", 400));
    }

}
