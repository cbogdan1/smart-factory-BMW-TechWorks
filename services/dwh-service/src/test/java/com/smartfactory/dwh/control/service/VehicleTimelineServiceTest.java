package com.smartfactory.dwh.control.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import com.smartfactory.common.dto.dwh.VehicleTimelineResponse;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.dwh.control.repository.VehicleTimelineRepository;
import com.smartfactory.dwh.entity.VehicleTimelineEntity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@QuarkusTest
class VehicleTimelineServiceTest {

    @Inject
    VehicleTimelineService vehicleTimelineService;

    @InjectMock
    VehicleTimelineRepository vehicleTimelineRepository;

    @Test
    @DisplayName("Should return mapped response list ordered chronologically when vehicle exists")
    void shouldReturnTimelineResponsesWhenVehicleExists() {
        // Arrange
        final String vehicleId = "VEH-001";
        final Instant t1 = Instant.parse("2026-08-01T10:00:00Z");
        final Instant t2 = Instant.parse("2026-08-01T11:00:00Z");
        final VehicleTimelineEntity entity1 =
                VehicleTimelineEntity.builder().vehicleId(vehicleId).eventId("EVT-1").eventType("order-created")
                        .sourceService("order-service").eventTimestamp(t1).build();
        final VehicleTimelineEntity entity2 =
                VehicleTimelineEntity.builder().vehicleId(vehicleId).eventId("EVT-2").eventType("production-planned")
                        .sourceService("planning-service").eventTimestamp(t2).build();
        when(vehicleTimelineRepository.findByVehicleId(vehicleId)).thenReturn(List.of(entity1, entity2));

        // Act
        final List<VehicleTimelineResponse> result = vehicleTimelineService.getVehicleTimeline(vehicleId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("order-created", result.getFirst().getEventType());
        assertEquals("order-service", result.get(0).getSourceService());
        assertEquals(t1, result.get(0).getEventTimestamp());
        assertEquals("production-planned", result.get(1).getEventType());
        assertEquals("planning-service", result.get(1).getSourceService());
        assertEquals(t2, result.get(1).getEventTimestamp());
        verify(vehicleTimelineRepository, times(1)).findByVehicleId(vehicleId);
    }

    @Test
    @DisplayName("Should throw BusinessException with 404 when no timeline entries exist for given vehicleId")
    void shouldThrowBusinessExceptionWhenVehicleNotFound() {
        // Arrange
        final String unknownVehicleId = "VEH-UNKNOWN";
        when(vehicleTimelineRepository.findByVehicleId(unknownVehicleId)).thenReturn(Collections.emptyList());

        // Act & Assert
        final BusinessException exception = assertThrows(BusinessException.class,
                () -> vehicleTimelineService.getVehicleTimeline(unknownVehicleId));
        assertTrue(exception.getMessage().contains("Timeline entries not found for " + unknownVehicleId));
        assertEquals(404, exception.getStatusCode());
        verify(vehicleTimelineRepository, times(1)).findByVehicleId(unknownVehicleId);
    }

    @Test
    @DisplayName("Should throw BusinessException with 400 when vehicleId is blank or null")
    void shouldThrowBusinessExceptionWhenVehicleIdIsInvalid() {
        // Act & Assert
        final BusinessException exception =
                assertThrows(BusinessException.class, () -> vehicleTimelineService.getVehicleTimeline(" "));
        assertEquals(400, exception.getStatusCode());
        verify(vehicleTimelineRepository, never()).findByVehicleId(anyString());
    }

    @Test
    @DisplayName("Should persist new timeline entry when eventId is not present in repository")
    void shouldSaveTimelineEntryWhenEventIsNew() {
        // Arrange
        final String vehicleId = "VEH-001";
        final String eventId = "EVT-100";
        final Instant timestamp = Instant.now();
        final DomainEvent<Object> domainEvent =
                new DomainEvent<>(eventId, "parts-reserved", "1.0", timestamp, "inventory-service", vehicleId, null);
        when(vehicleTimelineRepository.existsByEventId(eventId)).thenReturn(false);

        // Act
        vehicleTimelineService.saveTimelineEntry(domainEvent);

        // Assert
        final ArgumentCaptor<VehicleTimelineEntity> captor = ArgumentCaptor.forClass(VehicleTimelineEntity.class);
        verify(vehicleTimelineRepository, times(1)).persist(captor.capture());
        final VehicleTimelineEntity saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(vehicleId, saved.getVehicleId());
        assertEquals(eventId, saved.getEventId());
        assertEquals("parts-reserved", saved.getEventType());
        assertEquals("inventory-service", saved.getSourceService());
        assertEquals(timestamp, saved.getEventTimestamp());
    }

    @Test
    @DisplayName("Should skip persistence when eventId already exists (Idempotency check)")
    void shouldNotSaveTimelineEntryWhenEventAlreadyExists() {
        // Arrange
        final String eventId = "EVT-DUPLICATE";
        final DomainEvent<Object> domainEvent =
                new DomainEvent<>(eventId, "parts-reserved", "1.0", Instant.now(), "inventory-service", "VEH-001",
                        null);
        when(vehicleTimelineRepository.existsByEventId(eventId)).thenReturn(true);

        // Act
        vehicleTimelineService.saveTimelineEntry(domainEvent);

        // Assert
        verify(vehicleTimelineRepository, times(1)).existsByEventId(eventId);
        verify(vehicleTimelineRepository, never()).persist(any(VehicleTimelineEntity.class));
    }
}