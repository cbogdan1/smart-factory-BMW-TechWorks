package com.smartfactory.inventory.control;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;
import com.smartfactory.inventory.entity.Reservation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class CancelledReservationService {

    @Inject
    ReservationRepository reservationRepository;

    @Transactional(REQUIRES_NEW)
    public void saveCancelledReservation(final Reservation reservation) {
        log.info("Persisting cancelled reservation for demand ID: {}", reservation.getMaterialDemand().getDemandId());
        reservationRepository.persist(reservation);
    }
}