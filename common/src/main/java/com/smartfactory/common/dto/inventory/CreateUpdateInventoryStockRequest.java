package com.smartfactory.common.dto.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
//@Setter
public class CreateUpdateInventoryStockRequest {
    @NotNull
    @Min(value = 0, message = "Quantity must be >= 0")
    public Long availableQuantity;

    @NotNull
    @Min(value = 0, message = "Quantity must be >= 0")
    public Long reservedQuantity;

    @NotNull
    @Min(value = 0, message = "Quantity must be >= 0")
    public Long minimumQuantity;

    @NotNull
    @Min(value = 0, message = "Quantity must be >= 0")
    public Long maximumQuantity;

}
