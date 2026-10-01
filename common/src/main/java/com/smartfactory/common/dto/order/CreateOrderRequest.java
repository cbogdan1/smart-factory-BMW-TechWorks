package com.smartfactory.common.dto.order;

import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.common.enums.Priority;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    @NotBlank
    private String customerName;

    @Email
    private String customerEmail;

    @NotBlank
    private String vehicleModel;

    @NotBlank
    private String color;

    @NotNull
    private BatteryType batteryType;

    @NotNull
    private Priority priority;
}
