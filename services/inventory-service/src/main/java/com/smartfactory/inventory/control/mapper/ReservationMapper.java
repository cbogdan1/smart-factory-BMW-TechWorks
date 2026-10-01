package com.smartfactory.inventory.control.mapper;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.smartfactory.common.dto.inventory.ReservationResponse;
import com.smartfactory.common.enums.ReservationStatus;
import com.smartfactory.inventory.entity.MaterialDemand;
import com.smartfactory.inventory.entity.Reservation;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReservationMapper {

    public static ReservationResponse toResponse(final Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        final boolean shortage = reservation.getStatus() == ReservationStatus.CANCELLED;

        return new ReservationResponse(reservation.getReservationId(),
                reservation.getMaterialDemand() != null ? reservation.getMaterialDemand().getVehicleId() : null,
                reservation.getPart() != null ? reservation.getPart().getPartCode() : null,
                reservation.getQuantityReserved(), reservation.getStatus(), shortage);
    }

    public static Reservation cancelledFromDemand(final MaterialDemand demand){
        final Reservation reservation = new Reservation();

        reservation.setReservationId(
                "RES-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        reservation.setMaterialDemand(demand);
        reservation.setPart(demand.getRequiredPart());
        reservation.setQuantityReserved(0L);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setReservedAt(OffsetDateTime.now());
        return reservation;
    }
}
