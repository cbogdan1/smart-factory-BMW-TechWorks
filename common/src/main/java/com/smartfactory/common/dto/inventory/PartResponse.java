package com.smartfactory.common.dto.inventory;

import java.time.LocalDateTime;
import java.util.UUID;

import com.smartfactory.common.enums.UnitOfMeasure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartResponse {

    private UUID id;
    private String partId;
    private String partCode;
    private String partName;
    private String description;
    private UnitOfMeasure unitOfMeasure;
    private Boolean active;
    private LocalDateTime createdDate;
}