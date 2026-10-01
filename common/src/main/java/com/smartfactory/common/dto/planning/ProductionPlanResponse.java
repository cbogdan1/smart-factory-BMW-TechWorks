package com.smartfactory.common.dto.planning;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.enums.ProductionPlanStatus;
import com.smartfactory.common.enums.ProductionStage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionPlanResponse {
    private String planId;
    private String orderId;
    private String vehicleId;
    private String productionLine;
    private String vehicleModel;
    private Priority priority;
    private LocalDate plannedStartDate;
    private ProductionStage currentStage;
    private ProductionPlanStatus status;
    private LocalDateTime createdDate;
}