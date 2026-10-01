package com.smartfactory.inventory.control;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.smartfactory.inventory.entity.Reservation;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReservationRepository implements PanacheRepositoryBase<Reservation, UUID> {
    public Optional<Reservation> findByReservationId(final String reservationId) {
        return find("reservationId", reservationId).firstResultOptional();
    }

    public List<Reservation> findByVehicleId(final String vehicleId) {
        return list("materialDemand.vehicleId", vehicleId);
    }

    public boolean existsByDemandId(final String demandId) {
        return count("materialDemand.demandId", demandId) > 0;
    }

    public Optional<Reservation> findByDemandId(final String demandId) {
        return find("materialDemand.demandId", demandId).firstResultOptional();
    }
}
