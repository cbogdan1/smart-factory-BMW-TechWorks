package com.smartfactory.assembly.control;

import static com.smartfactory.common.enums.ProductionStage.ASSEMBLED;
import static com.smartfactory.common.enums.ProductionStage.BODY;
import static com.smartfactory.common.enums.ProductionStage.CREATED;
import static com.smartfactory.common.enums.ProductionStage.FINAL;
import static com.smartfactory.common.enums.ProductionStage.PAINT;
import static com.smartfactory.common.enums.ProductionStage.POWERTRAIN;
import static com.smartfactory.common.enums.ProductionStatus.IN_PRODUCTION;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_STAGE_TRANSITION_ERROR_MESSAGE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_VEHICLE_STATUS_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_VEHICLE_STATUS_ERROR_MESSAGE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import com.smartfactory.assembly.boundary.producer.MaterialRequestProducer;
import com.smartfactory.assembly.boundary.producer.VehicleAssembledProducer;
import com.smartfactory.assembly.boundary.producer.VehicleStageAdvanceProducer;
import com.smartfactory.assembly.control.repositories.VehicleProductionRepository;
import com.smartfactory.assembly.control.services.VehicleProductionService;
import com.smartfactory.assembly.entity.VehicleProduction;
import com.smartfactory.assembly.entity.machines.AssemblyStateMachine;
import com.smartfactory.common.dto.assembly.CreateVehicleProductionRequest;
import com.smartfactory.common.dto.assembly.VehicleProductionResponse;
import com.smartfactory.common.dto.assembly.VehicleProductionView;
import com.smartfactory.common.enums.ProductionStatus;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;
import com.smartfactory.common.payloads.inventory_service.PartsReservedPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleProductionServiceTest {

    @Mock
    VehicleProductionRepository repository;

    @Mock
    AssemblyStateMachine assemblyStateMachine;

    @Mock
    MaterialRequestProducer materialRequestProducer;

    @Mock
    VehicleAssembledProducer vehicleAssembledProducer;

    @Mock
    VehicleStageAdvanceProducer vehicleStageAdvanceProducer;

    @InjectMocks
    VehicleProductionService service;

    @Test
    void shouldCreateVehicleProductionWithDefaultValues() {
        final String vehicleId = "VEH-001";
        final String orderId = "ORD-001";
        final CreateVehicleProductionRequest request =
                new CreateVehicleProductionRequest(vehicleId, orderId, "BMW_I4", "LINE-1");

        final VehicleProductionResponse response = service.create(request);

        final ArgumentCaptor<VehicleProduction> captor = ArgumentCaptor.forClass(VehicleProduction.class);

        verify(repository).persist(captor.capture());

        final VehicleProduction persistedVehicle = captor.getValue();

        assertEquals(vehicleId, persistedVehicle.getVehicleId());
        assertEquals(orderId, persistedVehicle.getOrderId());
        assertEquals("LINE-1", persistedVehicle.getProductionLine());
        assertEquals(CREATED, persistedVehicle.getCurrentStage());
        assertEquals(ProductionStatus.CREATED, persistedVehicle.getStatus());
        assertNull(persistedVehicle.getStartedAt());
        assertNull(persistedVehicle.getCompletedAt());

        assertEquals(persistedVehicle.getVehicleId(), response.vehicleId());
        assertEquals(orderId, response.orderId());
        assertEquals(CREATED, response.currentStage());
        assertEquals(ProductionStatus.CREATED, response.status());
        assertNull(response.startedAt());
        assertNull(response.completedAt());
    }

    @Test
    void shouldPreserveIdentifiersFromRequest() {
        final String vehicleId = "VEH-001";
        final String orderId = "ORD-001";
        final CreateVehicleProductionRequest request =
                new CreateVehicleProductionRequest(vehicleId, orderId, "BMW_I4", "LINE-1");

        final VehicleProductionResponse response = service.create(request);

        assertEquals(vehicleId, response.vehicleId());
        assertEquals(orderId, response.orderId());

        final ArgumentCaptor<VehicleProduction> captor = ArgumentCaptor.forClass(VehicleProduction.class);
        verify(repository).persist(captor.capture());

        assertEquals(vehicleId, captor.getValue().getVehicleId());
        assertEquals(orderId, captor.getValue().getOrderId());
    }

    @Test
    void shouldFindVehicleProductionByVehicleId() {
        final VehicleProduction vehicleProduction = createVehicleProduction();

        when(repository.findByIdOptional(vehicleProduction.getVehicleId())).thenReturn(Optional.of(vehicleProduction));

        final VehicleProductionView view = service.findByVehicleId(vehicleProduction.getVehicleId());

        assertEquals(vehicleProduction.getVehicleId(), view.vehicleId());
        assertEquals(vehicleProduction.getOrderId(), view.orderId());
        assertEquals(vehicleProduction.getCurrentStage(), view.currentStage());
        assertEquals(vehicleProduction.getStatus(), view.status());
        assertEquals(vehicleProduction.getStartedAt(), view.startedAt());
        assertEquals(vehicleProduction.getCompletedAt(), view.completedAt());
    }

    @Test
    void shouldThrowBusinessExceptionWhenVehicleDoesNotExist() {
        final String vehicleId = "VEH-UNKNOWN";

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.empty());

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> service.findByVehicleId(vehicleId));

        assertEquals(String.format(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE, vehicleId), exception.getMessage());
        assertEquals(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void shouldRejectStartingProductionWhenVehicleStatusIsNotCreated() {
        final String vehicleId = "VEH-001";
        final String eventId = "EVT-NEW-001";
        final PartsReservedPayload payload = createPartsReservedPayload(vehicleId);
        final VehicleProduction vehicleProduction = createVehicleProduction();

        vehicleProduction.setStatus(IN_PRODUCTION);
        vehicleProduction.setCurrentStage(BODY);

        when(repository.findByStartedEventId(eventId)).thenReturn(Optional.empty());
        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> service.startProduction(eventId, payload));

        assertEquals(String.format(INVALID_VEHICLE_STATUS_ERROR_MESSAGE, vehicleId), exception.getMessage());
        assertEquals(INVALID_VEHICLE_STATUS_ERROR_CODE, exception.getErrorCode());
        assertEquals(409, exception.getStatusCode());

        verify(repository).findByStartedEventId(eventId);
        verify(repository).findByIdOptional(vehicleId);
        verify(repository, never()).persist(any(VehicleProduction.class));
        verifyNoInteractions(assemblyStateMachine);
    }

    @Test
    void shouldReturnAllVehicleProductionRecords() {
        final VehicleProduction firstVehicle = createVehicleProduction();
        final VehicleProduction secondVehicle = createVehicleProduction();

        when(repository.listAll()).thenReturn(List.of(firstVehicle, secondVehicle));

        final List<VehicleProductionResponse> responses = service.findAll();

        assertEquals(2, responses.size());
        assertEquals(firstVehicle.getVehicleId(), responses.get(0).vehicleId());
        assertEquals(secondVehicle.getVehicleId(), responses.get(1).vehicleId());
    }

    @Test
    void shouldReturnEmptyListWhenNoVehicleProductionsExist() {
        when(repository.listAll()).thenReturn(List.of());

        final List<VehicleProductionResponse> responses = service.findAll();

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }

    @Test
    void shouldAdvanceStageSuccessfully() {
        final VehicleProduction vehicleProduction = createVehicleProduction();
        final String vehicleId = vehicleProduction.getVehicleId();

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));
        when(assemblyStateMachine.getNextTransition(vehicleProduction.getCurrentStage())).thenReturn(BODY);

        final VehicleProductionResponse response = service.advanceStage(vehicleId);

        final ArgumentCaptor<VehicleProduction> captor = ArgumentCaptor.forClass(VehicleProduction.class);
        verify(repository).persist(captor.capture());

        final VehicleProduction persistedVehicle = captor.getValue();

        assertEquals(BODY, persistedVehicle.getCurrentStage());
        assertNull(persistedVehicle.getCompletedAt());

        assertEquals(BODY, response.currentStage());
        assertEquals(vehicleId, response.vehicleId());
        verify(vehicleAssembledProducer, never()).publishVehicleAssembled(any());
    }

    @Test
    void shouldPublishVehicleAssembledEventWhenAdvancingFromFinalToAssembled() {
        final VehicleProduction vehicleProduction = createVehicleProduction();

        vehicleProduction.setCurrentStage(FINAL);
        vehicleProduction.setStatus(IN_PRODUCTION);

        final String vehicleId = vehicleProduction.getVehicleId();

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));

        when(assemblyStateMachine.getNextTransition(FINAL)).thenReturn(ASSEMBLED);

        final VehicleProductionResponse response = service.advanceStage(vehicleId);

        final ArgumentCaptor<VehicleAssembledPayload> payloadCaptor =
                ArgumentCaptor.forClass(VehicleAssembledPayload.class);

        verify(vehicleAssembledProducer).publishVehicleAssembled(payloadCaptor.capture());

        final VehicleAssembledPayload payload = payloadCaptor.getValue();

        assertEquals(ASSEMBLED, vehicleProduction.getCurrentStage());
        assertEquals(ProductionStatus.ASSEMBLED, vehicleProduction.getStatus());
        assertNotNull(vehicleProduction.getCompletedAt());

        assertEquals(ASSEMBLED, response.currentStage());
        assertEquals(ProductionStatus.ASSEMBLED, response.status());
        assertEquals(vehicleProduction.getCompletedAt(), response.completedAt());

        assertEquals(vehicleId, payload.vehicleId());
        assertEquals(vehicleProduction.getOrderId(), payload.orderId());
        assertEquals(vehicleProduction.getVehicleModel(), payload.vehicleModel());
        assertEquals(vehicleProduction.getProductionLine(), payload.productionLine());
        assertEquals(vehicleProduction.getCompletedAt().toInstant(), payload.assembledAt());
    }

    @Test
    void shouldThrowBusinessExceptionWhenAdvancingUnknownVehicle() {
        final String vehicleId = "VEH-UNKNOWN";

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.empty());

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> service.advanceStage(vehicleId));

        assertEquals(String.format(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE, vehicleId), exception.getMessage());
    }

    @Test
    void shouldThrowBusinessExceptionWhenTransitionIsInvalid() {
        final VehicleProduction vehicleProduction = createVehicleProduction();
        final String vehicleId = vehicleProduction.getVehicleId();
        final String errorMessage = INVALID_STAGE_TRANSITION_ERROR_MESSAGE;

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));
        when(assemblyStateMachine.getNextTransition(vehicleProduction.getCurrentStage())).thenThrow(
                new BusinessException(errorMessage));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> service.advanceStage(vehicleId));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldStartProduction() {
        final String vehicleId = "VEH-001";
        final String eventId = "EVT-001";
        final PartsReservedPayload payload = createPartsReservedPayload(vehicleId);
        final VehicleProduction vehicleProduction = createVehicleProduction();

        vehicleProduction.setStatus(ProductionStatus.CREATED);
        vehicleProduction.setCurrentStage(CREATED);

        when(repository.findByStartedEventId(eventId)).thenReturn(Optional.empty());
        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));

        service.startProduction(eventId, payload);

        assertEquals(IN_PRODUCTION, vehicleProduction.getStatus());
        assertEquals(BODY, vehicleProduction.getCurrentStage());
        assertNotNull(vehicleProduction.getStartedAt());
        assertEquals(ZoneOffset.UTC, vehicleProduction.getStartedAt().getOffset());
        assertEquals(eventId, vehicleProduction.getStartedEventId());

        verify(repository).findByStartedEventId(eventId);
        verify(repository).findByIdOptional(vehicleId);
        verify(assemblyStateMachine).assertTransition(CREATED, BODY);
        verify(repository).persist(vehicleProduction);
    }

    @Test
    void shouldSkipDuplicateStartProductionEvent() {
        final String vehicleId = "VEH-001";
        final String eventId = "EVT-001";
        final OffsetDateTime originalStartedAt = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(5);
        final PartsReservedPayload payload = createPartsReservedPayload(vehicleId);
        final VehicleProduction vehicleProduction = createVehicleProduction();

        vehicleProduction.setStatus(IN_PRODUCTION);
        vehicleProduction.setCurrentStage(BODY);
        vehicleProduction.setStartedAt(originalStartedAt);
        vehicleProduction.setStartedEventId(eventId);

        when(repository.findByStartedEventId(eventId)).thenReturn(Optional.of(vehicleProduction));

        service.startProduction(eventId, payload);

        assertEquals(IN_PRODUCTION, vehicleProduction.getStatus());
        assertEquals(BODY, vehicleProduction.getCurrentStage());
        assertEquals(originalStartedAt, vehicleProduction.getStartedAt());
        assertEquals(eventId, vehicleProduction.getStartedEventId());

        verify(repository).findByStartedEventId(eventId);
        verify(repository, never()).findByIdOptional(vehicleId);
        verify(repository, never()).persist(any(VehicleProduction.class));
        verifyNoInteractions(assemblyStateMachine);
    }

    @Test
    void shouldThrowBusinessExceptionWhenStartingProductionForUnknownVehicle() {
        final String vehicleId = "VEH-UNKNOWN";
        final String eventId = "EVT-001";
        final PartsReservedPayload payload = createPartsReservedPayload(vehicleId);

        when(repository.findByStartedEventId(eventId)).thenReturn(Optional.empty());
        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.empty());

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> service.startProduction(eventId, payload));

        assertEquals(String.format(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE, vehicleId), exception.getMessage());
        assertEquals(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());

        verify(repository).findByStartedEventId(eventId);
        verify(repository).findByIdOptional(vehicleId);
        verify(repository, never()).persist(any(VehicleProduction.class));
        verifyNoInteractions(assemblyStateMachine);
    }

    @Test
    void shouldAdvanceStageAndPublishVehicleStageAdvancedEvent() {
        final VehicleProduction vehicleProduction = createVehicleProduction();
        vehicleProduction.setCurrentStage(BODY);
        vehicleProduction.setStatus(IN_PRODUCTION);

        final String vehicleId = vehicleProduction.getVehicleId();

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));
        when(assemblyStateMachine.getNextTransition(BODY)).thenReturn(PAINT);

        final VehicleProductionResponse response = service.advanceStage(vehicleId);

        final ArgumentCaptor<VehicleProduction> vehicleCaptor = ArgumentCaptor.forClass(VehicleProduction.class);
        final ArgumentCaptor<VehicleStageAdvancedPayload> payloadCaptor =
                ArgumentCaptor.forClass(VehicleStageAdvancedPayload.class);

        verify(repository).persist(vehicleCaptor.capture());
        verify(vehicleStageAdvanceProducer).publishAdvanceRequest(payloadCaptor.capture());
        verify(vehicleAssembledProducer, never()).publishVehicleAssembled(any());

        final VehicleProduction persistedVehicle = vehicleCaptor.getValue();
        final VehicleStageAdvancedPayload payload = payloadCaptor.getValue();

        assertEquals(PAINT, persistedVehicle.getCurrentStage());
        assertEquals(IN_PRODUCTION, persistedVehicle.getStatus());
        assertNull(persistedVehicle.getCompletedAt());

        assertEquals(vehicleId, response.vehicleId());
        assertEquals(PAINT, response.currentStage());
        assertEquals(IN_PRODUCTION, response.status());
        assertNull(response.completedAt());

        assertEquals(vehicleId, payload.vehicleId());
        assertEquals(vehicleProduction.getOrderId(), payload.orderId());
        assertEquals(BODY, payload.previousStage());
        assertEquals(PAINT, payload.currentStage());
        assertNotNull(payload.advancedAt());
    }

    @Test
    void shouldPublishCorrectPreviousAndCurrentStageWhenAdvancingFromPowertrainToFinal() {
        final VehicleProduction vehicleProduction = createVehicleProduction();
        vehicleProduction.setCurrentStage(POWERTRAIN);
        vehicleProduction.setStatus(IN_PRODUCTION);

        final String vehicleId = vehicleProduction.getVehicleId();

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));
        when(assemblyStateMachine.getNextTransition(POWERTRAIN)).thenReturn(FINAL);

        final VehicleProductionResponse response = service.advanceStage(vehicleId);

        final ArgumentCaptor<VehicleStageAdvancedPayload> payloadCaptor =
                ArgumentCaptor.forClass(VehicleStageAdvancedPayload.class);

        verify(repository).persist(vehicleProduction);
        verify(vehicleStageAdvanceProducer).publishAdvanceRequest(payloadCaptor.capture());
        verify(vehicleAssembledProducer, never()).publishVehicleAssembled(any());

        final VehicleStageAdvancedPayload payload = payloadCaptor.getValue();

        assertEquals(FINAL, vehicleProduction.getCurrentStage());
        assertEquals(FINAL, response.currentStage());

        assertEquals(vehicleId, payload.vehicleId());
        assertEquals(vehicleProduction.getOrderId(), payload.orderId());
        assertEquals(POWERTRAIN, payload.previousStage());
        assertEquals(FINAL, payload.currentStage());
        assertNotNull(payload.advancedAt());
    }

    @Test
    void shouldPublishStageAdvancedAndVehicleAssembledEventsWhenAdvancingFromFinalToAssembled() {
        final VehicleProduction vehicleProduction = createVehicleProduction();
        vehicleProduction.setCurrentStage(FINAL);
        vehicleProduction.setStatus(IN_PRODUCTION);

        final String vehicleId = vehicleProduction.getVehicleId();

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));
        when(assemblyStateMachine.getNextTransition(FINAL)).thenReturn(ASSEMBLED);

        service.advanceStage(vehicleId);
        
        verify(vehicleStageAdvanceProducer).publishAdvanceRequest(any(VehicleStageAdvancedPayload.class));
        verify(vehicleAssembledProducer).publishVehicleAssembled(any(VehicleAssembledPayload.class));
    }

    @Test
    void shouldNotPersistOrPublishWhenStatusIsAlreadyAssembled() {
        final VehicleProduction vehicleProduction = createVehicleProduction();
        vehicleProduction.setCurrentStage(ASSEMBLED);
        vehicleProduction.setStatus(ProductionStatus.ASSEMBLED);

        final String vehicleId = vehicleProduction.getVehicleId();
        final String errorMessage = String.format(INVALID_VEHICLE_STATUS_ERROR_MESSAGE, vehicleId);

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> service.advanceStage(vehicleId));

        assertEquals(errorMessage, exception.getMessage());

        verify(repository).findByIdOptional(vehicleId);
        verifyNoInteractions(assemblyStateMachine);
        verify(repository, never()).persist(any(VehicleProduction.class));
        verifyNoInteractions(vehicleStageAdvanceProducer);
        verifyNoInteractions(vehicleAssembledProducer);
    }

    @Test
    void shouldRejectAdvancingStageWhenVehicleStatusIsNotInProduction() {
        final VehicleProduction vehicleProduction = createVehicleProduction();
        vehicleProduction.setStatus(ProductionStatus.CREATED);
        vehicleProduction.setCurrentStage(CREATED);

        final String vehicleId = vehicleProduction.getVehicleId();

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> service.advanceStage(vehicleId));

        assertEquals(String.format(INVALID_VEHICLE_STATUS_ERROR_MESSAGE, vehicleId), exception.getMessage());
        assertEquals(INVALID_VEHICLE_STATUS_ERROR_CODE, exception.getErrorCode());
        assertEquals(409, exception.getStatusCode());

        verify(repository).findByIdOptional(vehicleId);
        verifyNoInteractions(assemblyStateMachine);
        verify(repository, never()).persist(any(VehicleProduction.class));
        verifyNoInteractions(vehicleStageAdvanceProducer);
        verifyNoInteractions(vehicleAssembledProducer);
    }

    @Test
    void shouldPersistVehicleWhenAdvancingFromFinalToAssembled() {
        final VehicleProduction vehicleProduction = createVehicleProduction();
        vehicleProduction.setCurrentStage(FINAL);
        vehicleProduction.setStatus(IN_PRODUCTION);

        final String vehicleId = vehicleProduction.getVehicleId();

        when(repository.findByIdOptional(vehicleId)).thenReturn(Optional.of(vehicleProduction));
        when(assemblyStateMachine.getNextTransition(FINAL)).thenReturn(ASSEMBLED);

        service.advanceStage(vehicleId);

        final ArgumentCaptor<VehicleProduction> vehicleCaptor = ArgumentCaptor.forClass(VehicleProduction.class);

        verify(repository).persist(vehicleCaptor.capture());

        final VehicleProduction persistedVehicle = vehicleCaptor.getValue();

        assertEquals(ASSEMBLED, persistedVehicle.getCurrentStage());
        assertEquals(ProductionStatus.ASSEMBLED, persistedVehicle.getStatus());
        assertNotNull(persistedVehicle.getCompletedAt());
    }

    private PartsReservedPayload createPartsReservedPayload(final String vehicleId) {
        return new PartsReservedPayload("RES-001", vehicleId, "PLAN-001", List.of(), "2026-08-15T10:00:00Z");
    }

    private VehicleProduction createVehicleProduction() {
        final VehicleProduction vehicleProduction = new VehicleProduction();

        vehicleProduction.setVehicleId("VEH-001");
        vehicleProduction.setOrderId("ORD-001");
        vehicleProduction.setVehicleModel("BMW_I4");
        vehicleProduction.setProductionLine("LINE-1");
        vehicleProduction.setCurrentStage(CREATED);
        vehicleProduction.setStatus(ProductionStatus.CREATED);
        vehicleProduction.setStartedAt(java.time.OffsetDateTime.now(ZoneOffset.UTC));
        vehicleProduction.setCompletedAt(null);
        vehicleProduction.setStatus(IN_PRODUCTION);

        return vehicleProduction;
    }
}