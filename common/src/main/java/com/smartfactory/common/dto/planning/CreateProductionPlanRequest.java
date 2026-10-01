package com.smartfactory.common.dto.planning;

import java.time.LocalDate;

import com.smartfactory.common.enums.Priority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductionPlanRequest {
    private String orderId;
    private String vehicleId;
    private Priority priority;
    private LocalDate plannedStartDate;
    private String vehicleModel;
}