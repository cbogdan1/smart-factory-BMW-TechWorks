package com.smartfactory.inventory.control;

import static org.mockito.Mockito.verify;

import com.smartfactory.inventory.entity.MaterialDemand;
import com.smartfactory.inventory.entity.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CancelledReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;

    @InjectMocks
    CancelledReservationService cancelledReservationService;

    @Test
    void shouldSaveCancelledReservation() {
        final Reservation reservation = new Reservation();
        final MaterialDemand demand = new MaterialDemand();
        demand.setDemandId("DMD-123");
        reservation.setMaterialDemand(demand);

        cancelledReservationService.saveCancelledReservation(reservation);

        verify(reservationRepository).persist(reservation);
    }
}