package com.smartfactory.order.boundary;

import static com.smartfactory.common.exception.OrderErrorCodes.ORDER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;

import com.smartfactory.common.dto.order.OrderProgressView;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.order.boundary.resource.OrderProgressResource;
import com.smartfactory.order.control.order_service.OrderProgressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderProgressResourceTest {

    @Mock
    OrderProgressService service;

    @InjectMocks
    OrderProgressResource resource;

    @Test
    void shouldGetOrderProgress() {

        final OrderProgressView view =
                new OrderProgressView("ORD-001", "VEH-001", OrderStatus.IN_PRODUCTION, ProductionStage.PAINT, null,
                        LocalDateTime.now());

        Mockito.when(service.getProgressView("ORD-001")).thenReturn(view);

        final OrderProgressView result = resource.getOrderProgress("ORD-001");

        assertNotNull(result);
        assertEquals(view, result);
    }

    @Test
    void shouldThrowWhenOrderNotFound() {

        final BusinessException exception = new BusinessException("Order not found: ORD-001", ORDER_NOT_FOUND, 404);

        Mockito.when(service.getProgressView("ORD-001")).thenThrow(exception);

        assertThrowsExactly(BusinessException.class, () -> resource.getOrderProgress("ORD-001"));
    }
}