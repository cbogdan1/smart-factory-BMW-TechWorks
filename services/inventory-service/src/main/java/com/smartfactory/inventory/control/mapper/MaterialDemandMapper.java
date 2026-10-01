package com.smartfactory.inventory.control.mapper;

import java.util.UUID;
import com.smartfactory.common.dto.inventory.MaterialDemandResponse;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.inventory.entity.MaterialDemand;
import com.smartfactory.inventory.entity.Part;
import com.smartfactory.inventory.entity.VehicleBom;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MaterialDemandMapper {
    public static MaterialDemand toEntity(final MaterialDemandResponse response, final Part part) {
        if (response == null) {
            return null;
        }

        final MaterialDemand demand = new MaterialDemand();

        demand.setId(response.getId());
        demand.setDemandId(response.getDemandId());
        demand.setPlanId(response.getPlanId());
        demand.setVehicleId(response.getVehicleId());
        demand.setRequiredPart(part);
        demand.setRequiredQuantity(response.getRequiredQuantity());
        demand.setPlannedDate(response.getPlannedDate());
        demand.setStatus(response.getStatus());
        demand.setCreatedDate(response.getCreatedDate());
        demand.setVehicleModel(response.getVehicleModel());
        demand.setEventId(response.getEventId());

        return demand;
    }

    public static MaterialDemand toEntity(final ProductionPlannedPayload payload, final String eventId, final VehicleBom entry) {
        final MaterialDemand demand = new MaterialDemand();
        demand.setDemandId("DMD-" + UUID.randomUUID());
        demand.setPlanId(payload.planId());
        demand.setVehicleId(payload.vehicleId());
        demand.setRequiredPart(entry.getPart());
        demand.setRequiredQuantity(entry.getQuantity());
        demand.setPlannedDate(payload.plannedStartDate());
        demand.setVehicleModel(payload.vehicleModel());
        demand.setEventId(eventId);

        return demand;
    }

    public static MaterialDemandResponse fromEntity(
            final MaterialDemand demand
    ) {
        final MaterialDemandResponse response = new MaterialDemandResponse();

        response.setId(demand.getId());
        response.setDemandId(demand.getDemandId());
        response.setPlanId(demand.getPlanId());
        response.setVehicleId(demand.getVehicleId());
        response.setRequiredPartCode(demand.getRequiredPart().getPartCode());
        response.setRequiredQuantity(demand.getRequiredQuantity());
        response.setPlannedDate(demand.getPlannedDate());
        response.setStatus(demand.getStatus());
        response.setCreatedDate(demand.getCreatedDate());
        response.setVehicleModel(demand.getVehicleModel());
        response.setEventId(demand.getEventId());

        return response;
    }

}
