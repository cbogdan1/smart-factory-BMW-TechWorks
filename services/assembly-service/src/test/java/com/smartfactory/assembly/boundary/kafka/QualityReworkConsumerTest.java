package com.smartfactory.assembly.boundary.kafka;

import static com.smartfactory.common.Topics.QUALITY_REWORK_REQUIRED;
import static com.smartfactory.common.enums.ProductionStage.PAINT;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;

import com.smartfactory.assembly.boundary.consumer.QualityReworkConsumer;
import com.smartfactory.assembly.control.services.VehicleProductionReworkService;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.QualityReworkRequiredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QualityReworkConsumerTest {

    @Mock
    VehicleProductionReworkService reworkService;

    @InjectMocks
    QualityReworkConsumer consumer;

    @Test
    void shouldDelegateValidEventToReworkService() {
        final String eventId = "EVT-REWORK-001";
        final String vehicleId = "VEH-001";

        final QualityReworkRequiredEvent event =
                createEvent(eventId, vehicleId, PAINT);

        final QualityReworkRequiredPayload payload =
                event.event().payload();

        consumer.consume(event);

        verify(reworkService).reenterRework(eventId, payload);
    }

    @Test
    void shouldRejectNullEvent() {
        assertInvalidEvent(null);
    }

    @Test
    void shouldRejectMissingDomainEvent() {
        assertInvalidEvent(new QualityReworkRequiredEvent(null));
    }

    @Test
    void shouldRejectMissingPayload() {
        final DomainEvent<QualityReworkRequiredPayload> domainEvent = new DomainEvent<>(
                        "EVT-REWORK-001",
                        QUALITY_REWORK_REQUIRED,
                        "quality-service",
                        "VEH-001",
                        null);

        assertInvalidEvent(new QualityReworkRequiredEvent(domainEvent));
    }

    @Test
    void shouldRejectMissingEventId() {
        assertInvalidEvent(createEvent(null, "VEH-001", PAINT));
    }

    @Test
    void shouldRejectMissingVehicleId() {
        assertInvalidEvent(createEvent("EVT-REWORK-001", null, PAINT));
    }

    @Test
    void shouldRejectMissingTargetStage() {
        assertInvalidEvent(createEvent("EVT-REWORK-001", "VEH-001", null));
    }

    private void assertInvalidEvent(final QualityReworkRequiredEvent event) {
        final BusinessException exception = assertThrows(BusinessException.class,
                () -> consumer.consume(event));

        assertEquals(NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_MESSAGE, exception.getMessage());

        assertEquals(NULL_QUALITY_REWORK_REQUIRED_EVENT_ERROR_CODE, exception.getErrorCode());

        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(reworkService);
    }

    private QualityReworkRequiredEvent createEvent(
            final String eventId,
            final String vehicleId,
            final ProductionStage targetStage) {
        final QualityReworkRequiredPayload payload =
                new QualityReworkRequiredPayload(
                        "INSP-001",
                        vehicleId,
                        72,
                        targetStage,
                        "Paint defect detected",
                        Instant.parse("2026-08-18T06:00:00Z"));

        final DomainEvent<QualityReworkRequiredPayload> domainEvent =
                new DomainEvent<>(
                        eventId,
                        QUALITY_REWORK_REQUIRED,
                        "quality-service",
                        vehicleId,
                        payload);

        return new QualityReworkRequiredEvent(domainEvent);
    }
}
