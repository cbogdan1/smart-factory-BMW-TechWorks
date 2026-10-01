package com.smartfactory.inventory.control.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartfactory.common.dto.inventory.ReservationResponse;
import com.smartfactory.common.enums.ReservationStatus;
import com.smartfactory.inventory.entity.MaterialDemand;
import com.smartfactory.inventory.entity.Part;
import com.smartfactory.inventory.entity.Reservation;
import org.junit.jupiter.api.Test;

class ReservationMapperTest {

    @Test
    void shouldMapToResponse() {
        MaterialDemand demand = new MaterialDemand();
        demand.setVehicleId("VEH-123");

        Part part = new Part();
        part.setPartCode("PART-123");

        Reservation reservation = new Reservation();
        reservation.setReservationId("RES-1");
        reservation.setMaterialDemand(demand);
        reservation.setPart(part);
        reservation.setQuantityReserved(10L);
        reservation.setStatus(ReservationStatus.ACTIVE);

        ReservationResponse response = ReservationMapper.toResponse(reservation);

        assertEquals("RES-1", response.getReservationId());
        assertEquals("VEH-123", response.getVehicleId());
        assertEquals("PART-123", response.getPartCode());
        assertEquals(10L, response.getQuantity());
        assertEquals(ReservationStatus.ACTIVE, response.getStatus());
        assertEquals(false, response.isShortage());
    }

    @Test
    void shouldMapToResponseAsShortageWhenCancelled() {
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CANCELLED);
        ReservationResponse response = ReservationMapper.toResponse(reservation);
        assertTrue(response.isShortage());
    }

    @Test
    void shouldReturnNullWhenMappingNullReservation() {
        assertNull(ReservationMapper.toResponse(null));
    }

    @Test
    void shouldCreateCancelledReservationFromDemand() {
        MaterialDemand demand = new MaterialDemand();
        Part part = new Part();
        part.setPartCode("P-1");
        demand.setRequiredPart(part);

        Reservation reservation = ReservationMapper.cancelledFromDemand(demand);

        assertNotNull(reservation.getReservationId());
        assertEquals(demand, reservation.getMaterialDemand());
        assertEquals(part, reservation.getPart());
        assertEquals(0L, reservation.getQuantityReserved());
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertNotNull(reservation.getReservedAt());
    }
}