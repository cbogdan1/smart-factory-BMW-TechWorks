package com.smartfactory.planning.utility;

import com.smartfactory.common.dto.planning.ProductionPlanResponse;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.planning.entity.ProductionPlan;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductionPlanMapper {
    public ProductionPlannedPayload toPayload(final ProductionPlan productionPlan) {
        return new ProductionPlannedPayload(productionPlan.getPlanId(), productionPlan.getOrderId(),
                productionPlan.getVehicleId(), productionPlan.getVehicleModel(), productionPlan.getProductionLine(),
                productionPlan.getPriority(), productionPlan.getPlannedStartDate());
    }

    public ProductionPlanResponse toResponse(final ProductionPlan productionPlan) {
        final ProductionPlanResponse response = new ProductionPlanResponse();

        response.setPlanId(productionPlan.getPlanId());
        response.setOrderId(productionPlan.getOrderId());
        response.setVehicleId(productionPlan.getVehicleId());
        response.setVehicleModel(productionPlan.getVehicleModel());
        response.setProductionLine(productionPlan.getProductionLine());
        response.setPriority(productionPlan.getPriority());
        response.setPlannedStartDate(productionPlan.getPlannedStartDate());
        response.setStatus(productionPlan.getStatus());
        response.setCurrentStage(productionPlan.getCurrentStage());
        response.setCreatedDate(productionPlan.getCreatedDate());

        return response;
    }
}