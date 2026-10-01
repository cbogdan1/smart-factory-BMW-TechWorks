package com.smartfactory.inventory.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.smartfactory.common.dto.inventory.ReservationResponse;
import com.smartfactory.inventory.control.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationResourceTest {

    @Mock
    ReservationService reservationService;

    @InjectMocks
    ReservationResource reservationResource;

    @Test
    void shouldGetReservationsByVehicleId() {
        final List<ReservationResponse> mockList = List.of(new ReservationResponse());
        when(reservationService.getReservationsByVehicleId("VEH-1")).thenReturn(mockList);

        final List<ReservationResponse> result = reservationResource.getReservationsByVehicleId("VEH-1");

        assertEquals(mockList, result);
        verify(reservationService).getReservationsByVehicleId("VEH-1");
    }

    @Test
    void shouldConsumeReservation() {
        final ReservationResponse mockResponse = new ReservationResponse();
        when(reservationService.consumeReservation("RES-1")).thenReturn(mockResponse);

        final ReservationResponse result = reservationResource.consumeReservation("RES-1");

        assertEquals(mockResponse, result);
        verify(reservationService).consumeReservation("RES-1");
    }
}