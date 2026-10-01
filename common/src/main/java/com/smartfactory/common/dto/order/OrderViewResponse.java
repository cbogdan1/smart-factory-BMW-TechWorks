package com.smartfactory.common.dto.order;

import com.smartfactory.common.enums.BatteryType;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Builder
public record OrderViewResponse(@Schema(example = "Fname Sname", required = true) String customerName,

                                @Schema(example = "Model X", required = true) String vehicleModel,

                                @Schema(example = "Color code", required = true) String color,

                                @Schema(example = "Range?", required = true) BatteryType batteryType

) {
}
