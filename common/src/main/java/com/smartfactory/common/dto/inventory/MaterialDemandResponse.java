package com.smartfactory.common.dto.inventory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.smartfactory.common.enums.DemandStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDemandResponse {

    private UUID id;

    private String demandId;

    private String planId;

    private String vehicleId;

    private String requiredPartCode;

    private Integer requiredQuantity;

    private LocalDate plannedDate;

    private DemandStatus status;

    private LocalDateTime createdDate;

    private String vehicleModel;

    private String eventId;
}