package com.smartfactory.order.control.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import com.smartfactory.common.dto.order.CreateOrderRequest;
import com.smartfactory.common.dto.order.OrderResponse;
import com.smartfactory.common.dto.order.OrderStatusUpdateResponse;
import com.smartfactory.common.dto.order.OrderViewResponse;
import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.common.payloads.order_service.OrderStatusUpdatedPayload;
import com.smartfactory.order.boundary.exception_handler.InvalidTransitionException;
import com.smartfactory.order.boundary.exception_handler.OrderNotFoundException;
import com.smartfactory.order.boundary.exception_handler.VehicleModelNotFoundException;
import com.smartfactory.order.control.mapper.OrderCreatedPayloadMapper;
import com.smartfactory.order.control.mapper.OrderStatusResponseMapper;
import com.smartfactory.order.control.mapper.OrderViewMapper;
import com.smartfactory.order.control.order_service.IdGeneratorService;
import com.smartfactory.order.control.order_service.OrderServiceImpl;
import com.smartfactory.order.control.repository.OrderRepository;
import com.smartfactory.order.control.validation_service.ValidationService;
import com.smartfactory.order.entity.Order;
import jakarta.enterprise.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    IdGeneratorService idGeneratorService;

    @Mock
    ValidationService validationService;

    @Mock
    OrderViewMapper viewMapper;

    @Mock
    private Event<OrderCreatedPayload> orderCreatedEvent;

    @Mock
    private Event<OrderStatusUpdatedPayload> orderStatusUpdatedEvent;


    private OrderServiceImpl orderServiceImpl;

    @Mock
    private OrderStatusResponseMapper statusMapper;
    @Mock
    private OrderCreatedPayloadMapper payloadMapper;
    private Order order;

    @BeforeEach
    public void setUp() {
        orderServiceImpl = new OrderServiceImpl(
                orderRepository,
                idGeneratorService,
                validationService,
                payloadMapper,
                statusMapper,
                viewMapper,
                orderCreatedEvent,
                orderStatusUpdatedEvent);

        order = new Order();
        order.setOrderId("ORD-000");
        order.setVehicleId("VH-000");
        order.setCustomerName("Test Customer");
        order.setCustomerEmail("test@test.com");
        order.setVehicleModel("BMW_x4");
        order.setColor("BLUE");
        order.setBatteryType(BatteryType.LONG_RANGE);
        order.setPriority(Priority.HIGH);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedDate(LocalDateTime.now());
        order.setLastModifiedDate(LocalDateTime.now());

    }

    @Test
    void shouldCreateOrder() {

        when(idGeneratorService.generateOrderId()).thenReturn("ORD-123");

        when(idGeneratorService.generateVehicleId()).thenReturn("VEH-456");

        Mockito.when(payloadMapper.toPayload(Mockito.any(Order.class))).thenReturn(
                new OrderCreatedPayload(order.getOrderId(), order.getVehicleId(), order.getCustomerName(),
                        order.getCustomerEmail(), order.getVehicleModel(), order.getColor(),
                        order.getBatteryType().name(), order.getPriority(), order.getStatus()));

        final CreateOrderRequest request = new CreateOrderRequest();

        request.setCustomerName("Matei");
        request.setCustomerEmail("matei@test.com");
        request.setVehicleModel("BMW_i4");
        request.setColor("Blue");
        request.setBatteryType(BatteryType.LONG_RANGE);
        request.setPriority(Priority.HIGH);

        final OrderResponse response = orderServiceImpl.createOrder(request);

        assertEquals("ORD-123", response.getOrderId());
        assertEquals("VEH-456", response.getVehicleId());

        assertEquals("Matei", response.getCustomerName());
        assertEquals("BMW_i4", response.getVehicleModel());
        assertEquals("Blue", response.getColor());

        assertEquals("matei@test.com", response.getCustomerEmail());

        assertEquals(Priority.HIGH, response.getPriority());

        assertNotNull(response.getLastModifiedDate());

        assertEquals(response.getCreatedDate(), response.getLastModifiedDate());

        assertEquals(BatteryType.LONG_RANGE, response.getBatteryType());

        assertEquals(OrderStatus.CREATED, response.getStatus());

        assertNotNull(response.getCreatedDate());

        verify(orderRepository).persist(any(Order.class));
        verify(payloadMapper)
                .toPayload(any(Order.class));

        verify(orderCreatedEvent).fire(any(OrderCreatedPayload.class));
    }

    @Test
    public void shouldThrowExceptionWhenConfigIsInvalid() {

        final CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Matei");
        request.setVehicleModel("BMW_x4");
        request.setColor("Blue");
        request.setBatteryType(BatteryType.LONG_RANGE);

        Mockito.doThrow(new VehicleModelNotFoundException("BMW_x4")).when(validationService)
                .validate(request.getVehicleModel(), request.getBatteryType());

        final VehicleModelNotFoundException exception =
                assertThrows(VehicleModelNotFoundException.class, () -> orderServiceImpl.createOrder(request));

        assertEquals("Unsupported vehicle model: BMW_x4", exception.getMessage());

        Mockito.verify(orderRepository, never()).persist(Mockito.any(Order.class));

    }

    @Test
    public void shouldGetByIdOk() {
        when(orderRepository.findByOrderId("ORD-000")).thenReturn(Optional.of(order));
        when(viewMapper.toResponse(order)).thenReturn(
                new OrderViewResponse(order.getCustomerName(), order.getVehicleModel(), order.getColor(),
                        order.getBatteryType()));

        final OrderViewResponse orderViewResponse = orderServiceImpl.getOrderById("ORD-000");

        assertNotNull(orderViewResponse);
        assertEquals(orderViewResponse, viewMapper.toResponse(this.order));
        verify(orderRepository).findByOrderId("ORD-000");

    }

    @Test
    public void shouldGetByIdKo() {
        when(orderRepository.findByOrderId(Mockito.anyString())).thenReturn(Optional.empty());

        assertThrowsExactly(OrderNotFoundException.class, () -> orderServiceImpl.getOrderById("ORD-001"));
        verify(orderRepository).findByOrderId("ORD-001");

    }

    @Test
    public void shouldChangeStatusOk() {
        when(orderRepository.findByOrderId("ORD-000")).thenReturn(Optional.of(order));
        when(statusMapper.toResponse(OrderStatus.PLANNED)).thenReturn(
                new OrderStatusUpdateResponse(OrderStatus.PLANNED, LocalDateTime.now()));

        final OrderStatusUpdateResponse response = orderServiceImpl.changeStatus("ORD-000", OrderStatus.PLANNED);

        assertNotNull(response);
        assertEquals(OrderStatus.PLANNED, response.currentStatus());
        assertNotNull(response.lastModifiedDate());
        assertEquals(OrderStatus.PLANNED, order.getStatus());

        verify(orderRepository).findByOrderId("ORD-000");

        verify(orderStatusUpdatedEvent)
                .fire(any(OrderStatusUpdatedPayload.class));
    }

    @Test
    public void shouldChangeStatusNotFoundKo() {
        when(orderRepository.findByOrderId("ORD-999")).thenReturn(Optional.empty());

        assertThrowsExactly(OrderNotFoundException.class,
                () -> orderServiceImpl.changeStatus("ORD-999", OrderStatus.PLANNED));

        verify(orderRepository).findByOrderId("ORD-999");

        verify(orderStatusUpdatedEvent, never())
                .fire(any());
    }

    @Test
    public void shouldChangeStatusInvalidTransitionKo() {
        when(orderRepository.findByOrderId("ORD-000")).thenReturn(Optional.of(order));

        assertThrowsExactly(InvalidTransitionException.class,
                () -> orderServiceImpl.changeStatus("ORD-000", OrderStatus.COMPLETED));

        verify(orderRepository).findByOrderId("ORD-000");

        verify(orderStatusUpdatedEvent, never())
                .fire(any());
    }

}