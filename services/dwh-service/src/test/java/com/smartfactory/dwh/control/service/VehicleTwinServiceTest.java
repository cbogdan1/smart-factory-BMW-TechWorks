package com.smartfactory.dwh.control.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import com.smartfactory.common.dto.dwh.VehicleTwinResponse;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.dwh.control.repository.EventStoreRepository;
import com.smartfactory.dwh.control.repository.VehicleTwinRepository;
import com.smartfactory.dwh.control.strategy.DigitalTwinUpdateStrategy;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@QuarkusTest
class VehicleTwinServiceTest {

    @InjectMock
    VehicleTwinRepository vehicleTwinRepository;

    @InjectMock
    EventStoreRepository eventStoreRepository;

    @Inject
    Instance<DigitalTwinUpdateStrategy> updateStrategies;

    @Inject
    VehicleTwinService vehicleTwinService;

    @BeforeEach
    void setUp() {
        reset(eventStoreRepository, vehicleTwinRepository);
        when(eventStoreRepository.existsByEventId(anyString())).thenReturn(false);
    }

    @Test
    @DisplayName("Should create twin on order-created event")
    void shouldCreateTwinOnOrderCreated() {
        // Arrange
        final String vehicleId = "VEH-001";
        final String eventId = "EVT-101";

        final OrderCreatedPayload payload =
                new OrderCreatedPayload("ORD-001", vehicleId, "John Doe", "john@test.com", "BMW_I4", "BLACK", "90kWh",
                        null, null);

        final DomainEvent<OrderCreatedPayload> event =
                new DomainEvent<>(eventId, "order-created", "ORDER_SERVICE", vehicleId, payload);

        when(vehicleTwinRepository.findByVehicleId(vehicleId)).thenReturn(null);

        // Act
        vehicleTwinService.processEvent(event);

        // Assert
        final ArgumentCaptor<VehicleTwinEntity> captor = ArgumentCaptor.forClass(VehicleTwinEntity.class);
        verify(vehicleTwinRepository).persist(captor.capture());

        final VehicleTwinEntity savedTwin = captor.getValue();
        assertNotNull(savedTwin);
        assertEquals(vehicleId, savedTwin.getVehicleId());
        assertEquals("ORD-001", savedTwin.getOrderId());
        assertEquals("BMW_I4", savedTwin.getVehicleModel());
        assertEquals("CREATED", savedTwin.getStatus());
        assertEquals(0, savedTwin.getReworkCount());
        assertNotNull(savedTwin.getLastUpdated());
    }

    @Test
    @DisplayName("Should update status and location on production-planned event")
    void shouldUpdateOnProductionPlanned() {
        // Arrange
        final String vehicleId = "VEH-001";
        final String eventId = "EVT-102";
        final ProductionPlannedPayload payload =
                new ProductionPlannedPayload("PLAN-001", "ORD-001", vehicleId, "BMW_I4", "LINE-2", null, null);
        final DomainEvent<ProductionPlannedPayload> event =
                new DomainEvent<>(eventId, "production-planned", "PLANNING_SERVICE", vehicleId, payload);

        final VehicleTwinEntity existingTwin =
                VehicleTwinEntity.builder().vehicleId(vehicleId).orderId("ORD-001").vehicleModel("BMW_I4")
                        .status("CREATED").reworkCount(0).build();
        when(vehicleTwinRepository.findByVehicleId(vehicleId)).thenReturn(existingTwin);

        // Act
        vehicleTwinService.processEvent(event);

        // Assert
        final ArgumentCaptor<VehicleTwinEntity> captor = ArgumentCaptor.forClass(VehicleTwinEntity.class);
        verify(vehicleTwinRepository).persist(captor.capture());
        final VehicleTwinEntity updatedTwin = captor.getValue();
        assertEquals("PLANNED", updatedTwin.getStatus());
        assertEquals("LINE-2", updatedTwin.getCurrentLocation());
        assertEquals("BMW_I4", updatedTwin.getVehicleModel());
    }

    @Test
    @DisplayName("Should update status on parts-reserved event")
    void shouldUpdateOnPartsReserved() {
        // Arrange
        final String vehicleId = "VEH-001";
        final String eventId = "EVT-103";
        final DomainEvent<Object> event =
                new DomainEvent<>(eventId, "parts-reserved", "INVENTORY_SERVICE", vehicleId, null);
        final VehicleTwinEntity existingTwin =
                VehicleTwinEntity.builder().vehicleId(vehicleId).status("PLANNED").reworkCount(0).build();
        when(vehicleTwinRepository.findByVehicleId(vehicleId)).thenReturn(existingTwin);

        // Act
        vehicleTwinService.processEvent(event);

        // Assert
        final ArgumentCaptor<VehicleTwinEntity> captor = ArgumentCaptor.forClass(VehicleTwinEntity.class);
        verify(vehicleTwinRepository).persist(captor.capture());
        assertEquals("MATERIALS_SECURED", captor.getValue().getStatus());
    }

    @Test
    @DisplayName("Should update status and current stage on vehicle-assembled event")
    void shouldUpdateOnVehicleAssembled() {
        // Arrange
        final String vehicleId = "VEH-001";
        final String eventId = "EVT-104";
        final DomainEvent<Object> event =
                new DomainEvent<>(eventId, "vehicle-assembled", "ASSEMBLY_SERVICE", vehicleId, null);
        final VehicleTwinEntity existingTwin =
                VehicleTwinEntity.builder().vehicleId(vehicleId).status("MATERIALS_SECURED").reworkCount(0).build();
        when(vehicleTwinRepository.findByVehicleId(vehicleId)).thenReturn(existingTwin);

        // Act
        vehicleTwinService.processEvent(event);

        // Assert
        final ArgumentCaptor<VehicleTwinEntity> captor = ArgumentCaptor.forClass(VehicleTwinEntity.class);
        verify(vehicleTwinRepository).persist(captor.capture());
        assertEquals("ASSEMBLED", captor.getValue().getStatus());
        assertEquals("ASSEMBLED", captor.getValue().getCurrentStage());
    }

    @Test
    @DisplayName("Should skip processing if eventId was already processed (Idempotency Check)")
    void shouldSkipDuplicateEvent() {
        // Arrange
        final String duplicateEventId = "EVT-DUPLICATE-001";
        final DomainEvent<Object> duplicateEvent =
                new DomainEvent<>(duplicateEventId, "order-created", "ORDER_SERVICE", "VEH-001", null);
        when(eventStoreRepository.existsByEventId(duplicateEventId)).thenReturn(true);

        // Act
        vehicleTwinService.processEvent(duplicateEvent);

        // Assert
        verify(vehicleTwinRepository, never()).persist(any(VehicleTwinEntity.class));
        verify(vehicleTwinRepository, never()).findByVehicleId(anyString());
    }

    @Test
    @DisplayName("Should return mapped VehicleTwinResponse when vehicle twin exists")
    void shouldReturnTwinResponseWhenVehicleExists() {
        // Arrange
        final String vehicleId = "VEH-001";
        final Instant now = Instant.now();

        final VehicleTwinEntity entity =
                VehicleTwinEntity.builder().vehicleId(vehicleId).orderId("ORD-500").vehicleModel("BMW IX")
                        .status("FINISHED").currentStage("QUALITY_CHECK").currentLocation("LINE_04")
                        .qualityStatus("PASSED").reworkCount(1).lastUpdated(now).build();

        when(vehicleTwinRepository.findByVehicleId(vehicleId)).thenReturn(entity);

        // Act
        final VehicleTwinResponse response = vehicleTwinService.getVehicleTwin(vehicleId);

        // Assert
        assertNotNull(response);
        assertEquals(vehicleId, response.getVehicleId());
        assertEquals("ORD-500", response.getOrderId());
        assertEquals("BMW IX", response.getVehicleModel());
        assertEquals("FINISHED", response.getStatus());
        assertEquals("QUALITY_CHECK", response.getCurrentStage());
        assertEquals("LINE_04", response.getCurrentLocation());
        assertEquals("PASSED", response.getQualityStatus());

        verify(vehicleTwinRepository, times(1)).findByVehicleId(vehicleId);
    }

    @Test
    @DisplayName("Should throw BusinessException with 404 when vehicle twin does not exist")
    void shouldThrowBusinessExceptionWhenVehicleNotFound() {
        // Arrange
        final String unknownVehicleId = "VEH-999";
        when(vehicleTwinRepository.findByVehicleId(unknownVehicleId)).thenReturn(null);
        // Act & Assert
        final BusinessException exception =
                assertThrows(BusinessException.class, () -> vehicleTwinService.getVehicleTwin(unknownVehicleId));
        assertTrue(exception.getMessage().contains("Vehicle twin not found for " + unknownVehicleId));
        assertEquals("VEHICLE_NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
        verify(vehicleTwinRepository, times(1)).findByVehicleId(unknownVehicleId);
    }

    @Test
    @DisplayName("Should throw BusinessException with 400 when vehicleId is blank or null")
    void shouldThrowBusinessExceptionWhenVehicleIdIsInvalid() {
        // Act & Assert
        final BusinessException exception =
                assertThrows(BusinessException.class, () -> vehicleTwinService.getVehicleTwin(" "));
        assertEquals(400, exception.getStatusCode());
        verify(vehicleTwinRepository, never()).findByVehicleId(anyString());
    }
}

