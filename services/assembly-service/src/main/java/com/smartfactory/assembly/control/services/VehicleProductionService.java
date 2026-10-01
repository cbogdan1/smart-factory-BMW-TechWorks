package com.smartfactory.assembly.control.services;

import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_VEHICLE_STATUS_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_VEHICLE_STATUS_ERROR_MESSAGE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import com.smartfactory.assembly.boundary.producer.MaterialRequestProducer;
import com.smartfactory.assembly.boundary.producer.VehicleAssembledProducer;
import com.smartfactory.assembly.boundary.producer.VehicleStageAdvanceProducer;
import com.smartfactory.assembly.control.repositories.VehicleProductionRepository;
import com.smartfactory.assembly.control.mapper.VehicleProductionMapper;
import com.smartfactory.assembly.control.mapper.VehicleProductionPayloadMapper;
import com.smartfactory.assembly.entity.VehicleProduction;
import com.smartfactory.assembly.entity.machines.AssemblyStateMachine;
import com.smartfactory.assembly.entity.mapper.StageMaterialMapper;
import com.smartfactory.common.dto.assembly.CreateVehicleProductionRequest;
import com.smartfactory.common.dto.assembly.VehicleProductionResponse;
import com.smartfactory.common.dto.assembly.VehicleProductionView;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.enums.ProductionStatus;
import com.smartfactory.common.enums.StageMaterial;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.MaterialRequestedPayload;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;
import com.smartfactory.common.payloads.inventory_service.PartsReservedPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VehicleProductionService {

    private final VehicleProductionRepository repository;
    private final AssemblyStateMachine assemblyStateMachine;
    private final MaterialRequestProducer materialRequestProducer;
    private final VehicleAssembledProducer vehicleAssembledProducer;
    private final VehicleStageAdvanceProducer vehicleStageAdvanceProducer;

    public VehicleProductionService(final VehicleProductionRepository repository,
            final AssemblyStateMachine assemblyStateMachine, final MaterialRequestProducer materialRequestProducer,
            final VehicleAssembledProducer vehicleAssembledProducer,
            final VehicleStageAdvanceProducer vehicleStageAdvanceProducer) {
        this.repository = repository;
        this.assemblyStateMachine = assemblyStateMachine;
        this.materialRequestProducer = materialRequestProducer;
        this.vehicleAssembledProducer = vehicleAssembledProducer;
        this.vehicleStageAdvanceProducer = vehicleStageAdvanceProducer;
    }

    @Transactional
    public VehicleProductionResponse create(final CreateVehicleProductionRequest request) {
        final VehicleProduction vehicleProduction = VehicleProductionMapper.fromRequest(request);

        repository.persist(vehicleProduction);

        return VehicleProductionMapper.toResponse(vehicleProduction);
    }

    public VehicleProductionView findByVehicleId(final String vehicleId) {
        return repository.findByIdOptional(vehicleId).map(VehicleProductionMapper::toView).orElseThrow(
                () -> new BusinessException(String.format(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE, vehicleId),
                        VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, 404));
    }

    public List<VehicleProductionResponse> findAll() {
        return repository.listAll().stream().map(VehicleProductionMapper::toResponse).toList();
    }

    @Transactional
    public void startProduction(final String eventId, final PartsReservedPayload payload) {
        if (repository.findByStartedEventId(eventId).isPresent()) {
            return;
        }

        final String vehicleId = payload.vehicleId();

        final VehicleProduction vehicleProduction = repository.findByIdOptional(vehicleId).orElseThrow(
                () -> new BusinessException(String.format(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE, vehicleId),
                        VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, 404));

        if (vehicleProduction.getStatus() != ProductionStatus.CREATED) {
            throw new BusinessException(String.format(INVALID_VEHICLE_STATUS_ERROR_MESSAGE, vehicleId),
                    INVALID_VEHICLE_STATUS_ERROR_CODE, 409);
        }

        assemblyStateMachine.assertTransition(vehicleProduction.getCurrentStage(), ProductionStage.BODY);

        vehicleProduction.setStatus(ProductionStatus.IN_PRODUCTION);
        vehicleProduction.setCurrentStage(ProductionStage.BODY);
        vehicleProduction.setStartedAt(OffsetDateTime.now(ZoneOffset.UTC));
        vehicleProduction.setStartedEventId(eventId);

        repository.persist(vehicleProduction);
    }

    @Transactional
    public VehicleProductionResponse advanceStage(final String vehicleId) {
        final VehicleProduction vehicleProduction = repository.findByIdOptional(vehicleId).orElseThrow(
                () -> new BusinessException(String.format(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE, vehicleId),
                        VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, 404));

        if (vehicleProduction.getStatus() != ProductionStatus.IN_PRODUCTION) {
            throw new BusinessException(String.format(INVALID_VEHICLE_STATUS_ERROR_MESSAGE, vehicleId),
                    INVALID_VEHICLE_STATUS_ERROR_CODE, 409);
        }

        final ProductionStage currentProductionStage = vehicleProduction.getCurrentStage();
        final ProductionStage nextProductionStage =
                assemblyStateMachine.getNextTransition(vehicleProduction.getCurrentStage());

        updateVehicleProduction(vehicleProduction, nextProductionStage);
        repository.persist(vehicleProduction);

        publishStageAdvanceRequest(vehicleProduction, currentProductionStage, nextProductionStage);
        publishVehicleAssembledRequest(vehicleProduction);
        publishMaterialRequest(vehicleProduction);

        return VehicleProductionMapper.toResponse(vehicleProduction);
    }

    private void updateVehicleProduction(final VehicleProduction vehicleProduction,
            final ProductionStage nextProductionStage) {
        if (nextProductionStage == ProductionStage.ASSEMBLED) {
            vehicleProduction.setStatus(ProductionStatus.ASSEMBLED);
            vehicleProduction.setCompletedAt(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        }

        vehicleProduction.setCurrentStage(nextProductionStage);
    }

    private void publishStageAdvanceRequest(final VehicleProduction vehicleProduction,
            final ProductionStage currentStage, final ProductionStage nextStage) {
        final VehicleStageAdvancedPayload vehicleStageAdvancedPayload =
                VehicleProductionPayloadMapper.toStageAdvancePayload(vehicleProduction, currentStage, nextStage);

        vehicleStageAdvanceProducer.publishAdvanceRequest(vehicleStageAdvancedPayload);
    }

    private void publishVehicleAssembledRequest(final VehicleProduction vehicleProduction) {
        if (vehicleProduction.getCurrentStage() == ProductionStage.ASSEMBLED) {
            final VehicleAssembledPayload vehicleAssembledPayload =
                    VehicleProductionPayloadMapper.toAssembledPayload(vehicleProduction);

            vehicleAssembledProducer.publishVehicleAssembled(vehicleAssembledPayload);
        }
    }

    private void publishMaterialRequest(final VehicleProduction vehicleProduction) {
        final StageMaterial requestedMaterial = StageMaterialMapper.getMaterialFor(vehicleProduction.getCurrentStage());

        if (requestedMaterial != null) {
            final MaterialRequestedPayload materialRequestedPayload =
                    VehicleProductionPayloadMapper.toMaterialRequestedPayload(vehicleProduction, requestedMaterial);

            materialRequestProducer.publishMaterialRequest(materialRequestedPayload);
        }
    }
}
