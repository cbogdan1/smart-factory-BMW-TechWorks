package com.smartfactory.order.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.QualityReworkRequiredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import com.smartfactory.order.boundary.consumer.QualityReworkConsumer;
import com.smartfactory.order.boundary.exception_handler.InvalidTransitionException;
import com.smartfactory.order.control.order_service.OrderProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QualityReworkConsumerTest {

    private static final String EVENT_ID = "EVT-REWORK-123";
    private static final String VEHICLE_ID = "VEH-000";
    private static final String INSPECTION_ID = "INSP-000";

    @Mock
    private OrderProgressService service;

    @InjectMocks
    private QualityReworkConsumer consumer;

    private QualityReworkRequiredPayload payload;
    private QualityReworkRequiredEvent wrapperEvent;

    @BeforeEach
    void setUp() {
        payload = new QualityReworkRequiredPayload(INSPECTION_ID, VEHICLE_ID, 30, ProductionStage.CREATED,
                "Paint defects detected", Instant.now());

        final DomainEvent<QualityReworkRequiredPayload> domainEvent =
                new DomainEvent<>(EVENT_ID, "quality-rework-required", "quality-service", VEHICLE_ID, payload);

        wrapperEvent = new QualityReworkRequiredEvent(domainEvent);
    }

    @Test
    void onQualityReworkRequiredSuccess() {
        doNothing().when(service).markOrderForRework(EVENT_ID, payload);

        consumer.onQualityReworkRequired(wrapperEvent);

        verify(service).markOrderForRework(eq(EVENT_ID), eq(payload));
    }

    @Test
    void onQualityReworkRequiredOrderNotFoundThrowsBusinessException() {
        final BusinessException notFoundException =
                new BusinessException("Order not found for vehicle " + VEHICLE_ID, "ORDER_NOT_FOUND", 404);

        doThrow(notFoundException).when(service).markOrderForRework(EVENT_ID, payload);

        final BusinessException thrown = assertThrowsExactly(BusinessException.class, () -> {
            consumer.onQualityReworkRequired(wrapperEvent);
        });

        assertEquals("ORDER_NOT_FOUND", thrown.getErrorCode());
        assertEquals(404, thrown.getStatusCode());
        verify(service).markOrderForRework(EVENT_ID, payload);
    }

    @Test
    void onQualityReworkRequiredInvalidTransitionThrowsException() {
        final InvalidTransitionException invalidTransition =
                new InvalidTransitionException("COMPLETED -> REWORK_REQUIRED");

        doThrow(invalidTransition).when(service).markOrderForRework(EVENT_ID, payload);

        final BusinessException thrown = assertThrowsExactly(InvalidTransitionException.class, () -> {
            consumer.onQualityReworkRequired(wrapperEvent);
        });

        assertEquals("INVALID_STATUS_TRANSITION", thrown.getErrorCode());
        assertEquals(409, thrown.getStatusCode());
        verify(service).markOrderForRework(EVENT_ID, payload);
    }

    @Test
    void onQualityReworkRequiredUnexpectedError() {
        final RuntimeException unexpectedError = new RuntimeException("Database connection timeout");

        doThrow(unexpectedError).when(service).markOrderForRework(EVENT_ID, payload);

        assertThrowsExactly(RuntimeException.class, () -> {
            consumer.onQualityReworkRequired(wrapperEvent);
        });

        verify(service).markOrderForRework(EVENT_ID, payload);
    }
}