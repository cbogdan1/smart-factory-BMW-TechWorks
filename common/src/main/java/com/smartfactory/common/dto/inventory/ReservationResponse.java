package com.smartfactory.common.dto.inventory;

import com.smartfactory.common.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private String reservationId;
    private String vehicleId;
    private String partCode;
    private Long quantity;
    private ReservationStatus status;
    private boolean shortage;
}
