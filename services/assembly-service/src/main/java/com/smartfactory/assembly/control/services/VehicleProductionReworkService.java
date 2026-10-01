package com.smartfactory.assembly.control.services;

import static com.smartfactory.common.enums.ProductionStatus.IN_PRODUCTION;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.EVENT_ALREADY_PROCESSED_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.EVENT_ALREADY_PROCESSED_ERROR_MESSAGE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_REWORK_STAGE_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_REWORK_STAGE_ERROR_MESSAGE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE;

import com.smartfactory.assembly.control.repositories.EventsProcessedRepository;
import com.smartfactory.assembly.control.repositories.VehicleProductionRepository;
import com.smartfactory.assembly.entity.EventProcessed;
import com.smartfactory.assembly.entity.VehicleProduction;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VehicleProductionReworkService {
    VehicleProductionRepository vehicleProductionRepository;
    EventsProcessedRepository eventsProcessedRepository;

    public VehicleProductionReworkService(final VehicleProductionRepository vehicleProductionRepository,
            final EventsProcessedRepository eventsProcessedRepository) {
        this.vehicleProductionRepository = vehicleProductionRepository;
        this.eventsProcessedRepository = eventsProcessedRepository;
    }

    @Transactional
    public void reenterRework(final String eventId, final QualityReworkRequiredPayload payload) {
        if (eventsProcessedRepository.isEventProcessed(eventId)) {
            throw new BusinessException(String.format(EVENT_ALREADY_PROCESSED_ERROR_MESSAGE, eventId),
                    EVENT_ALREADY_PROCESSED_ERROR_CODE, 409);
        }

        final VehicleProduction vehicleProduction = vehicleProductionRepository.findByVehicleId(payload.vehicleId())
                .orElseThrow(
                        () -> new BusinessException(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE + payload.vehicleId(),
                                VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, 404));

        saveVehicle(vehicleProduction, payload.targetStage());
        saveEvent(eventId);
    }

    private void saveVehicle(final VehicleProduction vehicleProduction, final ProductionStage targetStage) {
        if (vehicleProduction.getStatus() == IN_PRODUCTION && targetStage != ProductionStage.ASSEMBLED) {
            throw new BusinessException(String.format(INVALID_REWORK_STAGE_ERROR_MESSAGE, targetStage),
                    INVALID_REWORK_STAGE_ERROR_CODE, 409);
        }

        vehicleProduction.setCurrentStage(targetStage);
        vehicleProduction.setStatus(IN_PRODUCTION);
        vehicleProduction.setReworkCount(vehicleProduction.getReworkCount() + 1);
        vehicleProductionRepository.persist(vehicleProduction);
    }

    private void saveEvent(final String eventId) {
        final EventProcessed eventProcessed = new EventProcessed();
        eventProcessed.setEventId(eventId);
        eventsProcessedRepository.persist(eventProcessed);
    }
}
