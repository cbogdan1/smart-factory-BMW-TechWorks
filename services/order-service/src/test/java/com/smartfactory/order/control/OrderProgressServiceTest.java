package com.smartfactory.order.control;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.order_service.OrderCompletedPayload;
import com.smartfactory.common.payloads.order_service.OrderStatusUpdatedPayload;
import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import com.smartfactory.order.boundary.exception_handler.InvalidTransitionException;
import com.smartfactory.order.control.mapper.OrderCompletedPayloadMapper;
import com.smartfactory.order.control.order_service.OrderProgressService;
import com.smartfactory.order.control.publisher.OrderEventPublisher;
import com.smartfactory.order.control.repository.OrderRepository;
import com.smartfactory.order.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderProgressServiceTest {

    private final String EVENT_ID = "EVT-123";
    private final String VEHICLE_ID = "VEH-000";
    @Mock
    OrderRepository repo;
    @Mock
    OrderEventPublisher publisher;
    @Mock
    OrderCompletedPayloadMapper payloadMapper;
    @InjectMocks
    OrderProgressService service;
    private Order order;
    private QualityApprovedPayload payload;
    private OrderCompletedPayload completedPayload;
    private QualityReworkRequiredPayload reworkPayload;

    @BeforeEach
    public void setUp() {
        order = new Order();
        order.setOrderId("ORD-000");
        order.setVehicleId(VEHICLE_ID);
        order.setStatus(OrderStatus.REWORK_REQUIRED);
        payload = new QualityApprovedPayload("INSP-000", VEHICLE_ID, 30, Instant.now());
        reworkPayload = new QualityReworkRequiredPayload("INSP-OOO", VEHICLE_ID, 30, ProductionStage.CREATED, "REASON",
                Instant.now());
        completedPayload =
                OrderCompletedPayload.builder().orderId("ORD-000").vehicleId(VEHICLE_ID).status(OrderStatus.COMPLETED)
                        .build();
    }

    @Test
    public void completeOrderFromQualityApprovalOk() {
        when(repo.isEventProcessed(EVENT_ID)).thenReturn(false);
        when(repo.findByVehicleId(VEHICLE_ID)).thenReturn(Optional.of(order));
        when(payloadMapper.toPayload(order)).thenReturn(completedPayload);

        service.completeOrderFromQualityApproval(EVENT_ID, payload);

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertNotNull(order.getCompletionDate());
        assertNotNull(order.getLastModifiedDate());

        verify(repo).update(order);
        verify(repo).markEventProcessed(EVENT_ID);
        verify(publisher).publishOrderCompleted(completedPayload);
    }

    @Test
    public void completeOrderFromQualityApprovalIdempotentOk() {
        when(repo.isEventProcessed(EVENT_ID)).thenReturn(true);

        service.completeOrderFromQualityApproval(EVENT_ID, payload);

        verify(repo, never()).findByVehicleId(Mockito.anyString());
        verify(repo, never()).update(Mockito.any(Order.class));
        verify(publisher, never()).publishOrderCompleted(Mockito.any());
    }

    @Test
    public void completeOrderFromQualityApprovalNotFoundKo() {
        when(repo.isEventProcessed(EVENT_ID)).thenReturn(false);
        when(repo.findByVehicleId(VEHICLE_ID)).thenReturn(Optional.empty());

        assertThrowsExactly(BusinessException.class, () -> {
            service.completeOrderFromQualityApproval(EVENT_ID, payload);
        });

        verify(repo, never()).update(Mockito.any(Order.class));
        verify(publisher, never()).publishOrderCompleted(Mockito.any());
    }

    @Test
    public void completeOrderFromQualityApprovalInvalidTransitionKo() {
        order.setStatus(OrderStatus.FAILED);

        when(repo.isEventProcessed(EVENT_ID)).thenReturn(false);
        when(repo.findByVehicleId(VEHICLE_ID)).thenReturn(Optional.of(order));

        assertThrowsExactly(InvalidTransitionException.class, () -> {
            service.completeOrderFromQualityApproval(EVENT_ID, payload);
        });

        verify(repo, never()).update(Mockito.any(Order.class));
        verify(repo, never()).markEventProcessed(Mockito.any(String.class));
        verify(publisher, never()).publishOrderCompleted(Mockito.any());
    }

    @Test
    public void completeOrderFromQualityApprovalAlreadyCompletedKo() {
        order.setStatus(OrderStatus.COMPLETED);

        when(repo.isEventProcessed(EVENT_ID)).thenReturn(false);
        when(repo.findByVehicleId(VEHICLE_ID)).thenReturn(Optional.of(order));

        final BusinessException ex = assertThrowsExactly(InvalidTransitionException.class, () -> {
            service.completeOrderFromQualityApproval(EVENT_ID, payload);
        });

        assertEquals("INVALID_STATUS_TRANSITION", ex.getErrorCode());
        assertEquals(409, ex.getStatusCode());
        verify(repo, never()).update(Mockito.any(Order.class));
    }

    @Test
    public void completeOrderFromQualityApprovalQualityCheckOk() {
        order.setStatus(OrderStatus.QUALITY_CHECK);

        when(repo.isEventProcessed(EVENT_ID)).thenReturn(false);
        when(repo.findByVehicleId(VEHICLE_ID)).thenReturn(Optional.of(order));
        when(payloadMapper.toPayload(order)).thenReturn(completedPayload);

        service.completeOrderFromQualityApproval(EVENT_ID, payload);

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        verify(publisher).publishOrderCompleted(Mockito.any());
    }

    @Test
    public void markOrderForReworkOk() {
        order.setStatus(OrderStatus.QUALITY_CHECK);

        when(repo.isEventProcessed(EVENT_ID)).thenReturn(false);
        when(repo.findByVehicleId(VEHICLE_ID)).thenReturn(Optional.of(order));

        service.markOrderForRework(EVENT_ID, reworkPayload);

        assertEquals(OrderStatus.REWORK_REQUIRED, order.getStatus());
        assertNotNull(order.getLastModifiedDate());

        verify(repo).update(order);
        verify(repo).markEventProcessed(EVENT_ID);
        verify(publisher).publishOrderStatusUpdated(Mockito.any(OrderStatusUpdatedPayload.class));
    }

    @Test
    public void markOrderForReworkIdempotentOk() {
        when(repo.isEventProcessed(EVENT_ID)).thenReturn(true);

        service.markOrderForRework(EVENT_ID, reworkPayload);

        verify(repo, never()).findByVehicleId(Mockito.anyString());
        verify(repo, never()).update(Mockito.any(Order.class));
        verify(publisher, never()).publishOrderStatusUpdated(Mockito.any());
    }

    @Test
    public void markOrderForReworkNotFoundKo() {
        when(repo.isEventProcessed(EVENT_ID)).thenReturn(false);
        when(repo.findByVehicleId(VEHICLE_ID)).thenReturn(Optional.empty());

        assertThrowsExactly(BusinessException.class, () -> {
            service.markOrderForRework(EVENT_ID, reworkPayload);
        });

        verify(repo, never()).update(Mockito.any(Order.class));
        verify(publisher, never()).publishOrderStatusUpdated(Mockito.any());
    }

    @Test
    public void markOrderForReworkInvalidTransitionKo() {
        order.setStatus(OrderStatus.COMPLETED);

        when(repo.isEventProcessed(EVENT_ID)).thenReturn(false);
        when(repo.findByVehicleId(VEHICLE_ID)).thenReturn(Optional.of(order));

        assertThrowsExactly(InvalidTransitionException.class, () -> {
            service.markOrderForRework(EVENT_ID, reworkPayload);
        });

        verify(repo, never()).update(Mockito.any(Order.class));
        verify(repo, never()).markEventProcessed(Mockito.any(String.class));
        verify(publisher, never()).publishOrderStatusUpdated(Mockito.any());
    }
}