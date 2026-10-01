package com.smartfactory.inventory.control;

import java.util.List;

import com.smartfactory.inventory.control.mapper.MaterialDemandMapper;
import com.smartfactory.inventory.entity.MaterialDemand;
import com.smartfactory.inventory.entity.VehicleBom;
import lombok.extern.slf4j.Slf4j;
import com.smartfactory.common.dto.inventory.MaterialDemandResponse;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Slf4j
@ApplicationScoped
public class MaterialDemandService {

    @Inject
    MaterialDemandRepository materialDemandRepository;

    @Inject
    VehicleBomRepository vehicleBomRepository;

    /**
     * Get all material demands
     * @return list of all MaterialDemand objects as MaterialDemandResponse objects
     */
    public List<MaterialDemandResponse> getAllMaterialDemands() {
        return materialDemandRepository
                .listAll()
                .stream()
                .map(MaterialDemandMapper::fromEntity)
                .toList();
    }

    /**
     * Get upcoming demands
     * @return list of MaterialDemandResponse objects with plannedDate >= today's date
     */
    public List<MaterialDemandResponse> getUpcomingDemands() {
        return materialDemandRepository
                .findUpcomingDemands()
                .stream()
                .map(MaterialDemandMapper::fromEntity)
                .toList();
    }

    /**
     * Get demands based on vehicle id
     * @param vehicleId the id of the vehicle for which we want to retrieve all material demands
     * @return a list of MaterialDemandResponse objects with vehicle id = vehicleId
     */
    public List<MaterialDemandResponse> getDemandsByVehicleId(
            final String vehicleId
    ) {
        return materialDemandRepository
                .findByVehicleId(vehicleId)
                .stream()
                .map(MaterialDemandMapper::fromEntity)
                .toList();
    }

    /**
     * Get material demand by the demand id
     * @param demandId the demand id for which we want to get the material demand record
     * @return a material demand response containing the record with the required demand id
     */
    public MaterialDemandResponse findByDemandId(final String demandId) {
        return materialDemandRepository.findByDemandId(demandId).map(MaterialDemandMapper::fromEntity).orElse(null);
    }

    /**
     * Record material demands based on a ProductionPlannedPayload, and an eventId.
     * A material demand record will be generated and persisted in the Material Demand Repository
     * for every BOM entry (aka car part) corresponding to the vehicle model specified in the payload
     *
     * @param payload the production planned payload containing useful information about
     *                the vehicle to be made, such as the vehicle model
     * @param eventId the id of the Kafka event that caused these demand records to be created
     */
    @Transactional
    public boolean recordDemand(
            final ProductionPlannedPayload payload,
            final String eventId
    ) {
        if(materialDemandRepository.existsByEventId(eventId)){
            log.info("Material demans for event {} already exists. Skipping.", eventId);
            return false;
        }

        // Find the BOM for this vehicle model
        final List<VehicleBom> bom =
                vehicleBomRepository.findByVehicleModel(payload.vehicleModel());


        // Ignore unknown vehicle models
        if (bom.isEmpty()) {
            log.warn(
                    "No BOM found for vehicle model {}",
                    payload.vehicleModel()
            );
            return false;
        }

        // Create one MaterialDemand for every BOM entry (BOM entry = 1 type of part)
        for (final VehicleBom entry : bom) {

            final MaterialDemand demand =
                    MaterialDemandMapper.toEntity(
                            payload,
                            eventId,
                            entry
                    );

            materialDemandRepository.persist(demand);
            // here the demand's demandId can be changed to be DMD + id if needed
        }

        log.info(
                "Created {} material demand rows for event {}",
                bom.size(),
                eventId
        );
        return true;
    }

    /**
     * Get material demand records by their planId
     * @param planId the planId for which we want to get all material records
     * @return list of material demands with planId = required plan id
     */
    public List<MaterialDemandResponse> getByPlanId(
            final String planId
    ) {
        return materialDemandRepository
                .findByPlanId(planId)
                .stream()
                .map(MaterialDemandMapper::fromEntity)
                .toList();
    }

}
