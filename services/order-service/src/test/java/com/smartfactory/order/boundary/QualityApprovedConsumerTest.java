package com.smartfactory.order.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.QualityApprovedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;
import com.smartfactory.order.boundary.consumer.QualityApprovedConsumer;
import com.smartfactory.order.control.order_service.OrderProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QualityApprovedConsumerTest {

    private final String EVENT_ID = "EVT-123";
    private final String VEHICLE_ID = "VEH-000";
    @Mock
    OrderProgressService service;
    @InjectMocks
    QualityApprovedConsumer consumer;
    private DomainEvent<QualityApprovedPayload> domainEvent;
    private QualityApprovedPayload payload;
    private QualityApprovedEvent qualityApprovedEvent;

    @BeforeEach
    public void setUp() {
        payload = new QualityApprovedPayload("INSP-00", VEHICLE_ID, 30, Instant.now());
        domainEvent = new DomainEvent<>(EVENT_ID, Topics.QUALITY_APPROVED, "quality-service", VEHICLE_ID, payload);
        qualityApprovedEvent = new QualityApprovedEvent(domainEvent);
    }

    @Test
    public void onQualityApprovedOk() {
        consumer.onQualityApproved(qualityApprovedEvent);

        verify(service).completeOrderFromQualityApproval(EVENT_ID, payload);
    }

    @Test
    public void onQualityApprovedBusinessExceptionKo() {
        final BusinessException expectedException = new BusinessException("Not found", "ORDER_NOT_FOUND", 404);

        doThrow(expectedException).when(service).completeOrderFromQualityApproval(EVENT_ID, payload);

        final BusinessException thrown = assertThrowsExactly(BusinessException.class, () -> {
            consumer.onQualityApproved(qualityApprovedEvent);
        });

        assertEquals("ORDER_NOT_FOUND", thrown.getErrorCode());
        assertEquals(404, thrown.getStatusCode());
    }

    @Test
    void onQualityApprovedNullPayloadKo() {
        final DomainEvent<QualityApprovedPayload> corruptEvent =
                new DomainEvent<>(EVENT_ID, Topics.QUALITY_APPROVED, "quality-service", VEHICLE_ID, null);
        doThrow(new BusinessException("Failed to complete order from quality approval", "ORDER_COMPLETION_ERROR")).when(
                service).completeOrderFromQualityApproval(any(), any());

        assertThrowsExactly(BusinessException.class, () -> {
            consumer.onQualityApproved(new QualityApprovedEvent(corruptEvent));
        });
    }

}