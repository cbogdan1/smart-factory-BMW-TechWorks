package com.smartfactory.order.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;

import com.smartfactory.common.dto.order.CreateOrderRequest;
import com.smartfactory.common.dto.order.OrderStatusUpdateRequest;
import com.smartfactory.common.dto.order.OrderStatusUpdateResponse;
import com.smartfactory.common.dto.order.OrderViewResponse;
import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.order.boundary.exception_handler.InvalidTransitionException;
import com.smartfactory.order.boundary.exception_handler.OrderNotFoundException;
import com.smartfactory.order.boundary.resource.OrderResource;
import com.smartfactory.order.control.mapper.OrderStatusResponseMapper;
import com.smartfactory.order.control.mapper.OrderViewMapper;
import com.smartfactory.order.control.order_service.OrderServiceImpl;
import com.smartfactory.order.entity.Order;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderResourceTest {
    @Mock
    OrderServiceImpl orderServiceImpl;
    @Mock
    OrderViewMapper viewMapper;

    @InjectMocks
    OrderResource orderResource;
    private OrderStatusResponseMapper orderStatusMapper;
    private CreateOrderRequest orderRequest;
    private OrderViewResponse view;
    private Order order;
    private OrderStatusUpdateRequest updateRequest;
    private OrderStatusUpdateResponse statusResponse;

    @BeforeEach
    public void setUp() {
        order = new Order();
        order.setOrderId("ORD-000");
        order.setVehicleId("VH-000");
        order.setCustomerName("Test Customer");
        order.setVehicleModel("BMW");
        order.setColor("BLUE");
        order.setBatteryType(BatteryType.LONG_RANGE);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedDate(LocalDateTime.now());
        Mockito.when(viewMapper.toResponse(order)).thenReturn(
                new OrderViewResponse(order.getCustomerName(), order.getVehicleModel(), order.getColor(),
                        order.getBatteryType()));
        view = viewMapper.toResponse(order);
        orderRequest = new CreateOrderRequest();
        orderRequest.setCustomerName("Test Customer");

        orderStatusMapper = new OrderStatusResponseMapper();
        updateRequest = new OrderStatusUpdateRequest(OrderStatus.PLANNED);
        statusResponse = orderStatusMapper.toResponse(OrderStatus.PLANNED);

    }

    @Test
    public void getOrderByIdOk() {
        Mockito.when(orderServiceImpl.getOrderById("ORD-000")).thenReturn(view);

        final Response response = orderResource.getOrderById("ORD-000");

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), view);

    }

    @Test
    public void getOrderByIdKo() {
        Mockito.when(orderServiceImpl.getOrderById("ORD-999"))
                .thenThrow(new OrderNotFoundException("Order not found: ORD-999"));

        assertThrowsExactly(OrderNotFoundException.class, () -> {
            orderResource.getOrderById("ORD-999");
        });

    }

    @Test
    public void setOrderStatusOk() {
        Mockito.when(orderServiceImpl.changeStatus("ORD-000", OrderStatus.PLANNED)).thenReturn(statusResponse);

        final Response response = orderResource.setOrderStatus("ORD-000", updateRequest);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), statusResponse);
    }

    @Test
    public void setOrderStatusInvalidTransitionKo() {
        final OrderStatusUpdateRequest invalidRequest = new OrderStatusUpdateRequest(OrderStatus.COMPLETED);

        Mockito.when(orderServiceImpl.changeStatus("ORD-000", OrderStatus.COMPLETED))
                .thenThrow(new InvalidTransitionException("Invalid transition from CREATED to COMPLETED"));

        assertThrowsExactly(InvalidTransitionException.class, () -> {
            orderResource.setOrderStatus("ORD-000", invalidRequest);
        });
    }

    @Test
    public void setOrderStatusNotFoundKo() {
        Mockito.when(orderServiceImpl.changeStatus("ORD-999", OrderStatus.PLANNED))
                .thenThrow(new OrderNotFoundException("Order not found: ORD-999"));

        assertThrowsExactly(OrderNotFoundException.class, () -> {
            orderResource.setOrderStatus("ORD-999", updateRequest);
        });
    }

}
