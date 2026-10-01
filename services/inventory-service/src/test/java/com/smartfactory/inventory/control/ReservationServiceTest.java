package com.smartfactory.inventory.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.smartfactory.common.dto.inventory.ReservationResponse;
import com.smartfactory.common.enums.ReservationStatus;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;
import com.smartfactory.inventory.boundary.LowStockPublisher;
import com.smartfactory.inventory.boundary.ShortageEventPublisher;
import com.smartfactory.inventory.control.mapper.ReservationMapper;
import com.smartfactory.inventory.entity.InventoryStock;
import com.smartfactory.inventory.entity.MaterialDemand;
import com.smartfactory.inventory.entity.Part;
import com.smartfactory.inventory.entity.Reservation;

class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    MaterialDemandRepository materialDemandRepository;

    @Mock
    InventoryStockRepository inventoryStockRepository;

    @Mock
    ShortageEventPublisher shortageEventPublisher;

    @Mock
    CancelledReservationService cancelledReservationService;

    @Mock
    LowStockPublisher lowStockPublisher;

    @Mock
    MaterialDemand demand;

    @Mock
    InventoryStock stock;

    @Mock
    Part part;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionWhenNoDemandsExist() {
        String eventId = "EVT-123";

        when(materialDemandRepository.findByEventId(eventId))
                .thenReturn(List.of());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> reservationService.reserveMaterialForDemand(eventId)
        );

        assertEquals("MATERIAL_DEMANDS_NOT_FOUND", exception.getErrorCode());

        verifyNoInteractions(inventoryStockRepository);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void shouldReturnExistingReservationWhenAlreadyReserved() {
        String eventId = "EVT-123";
        String demandId = "DMD-123";

        Reservation existingReservation = new Reservation();

        when(materialDemandRepository.findByEventId(eventId))
                .thenReturn(List.of(demand));

        when(demand.getDemandId())
                .thenReturn(demandId);

        when(reservationRepository.existsByDemandId(demandId))
                .thenReturn(true);

        when(reservationRepository.findByDemandId(demandId))
                .thenReturn(Optional.of(existingReservation));

        ReservationResponse response = mock(ReservationResponse.class);

        try (var mockedMapper = mockStatic(ReservationMapper.class)) {
            mockedMapper.when(() ->
                    ReservationMapper.toResponse(existingReservation)
            ).thenReturn(response);

            List<ReservationResponse> result =
                    reservationService.reserveMaterialForDemand(eventId);

            assertEquals(1, result.size());
            assertSame(response, result.get(0));

            verify(reservationRepository)
                    .findByDemandId(demandId);

            verify(inventoryStockRepository, never())
                    .findByPartId(any());

            verifyNoInteractions(shortageEventPublisher);
            verifyNoInteractions(cancelledReservationService);
        }
    }

    @Test
    void shouldThrowExceptionWhenInventoryDoesNotExist() {
        String eventId = "EVT-123";

        when(materialDemandRepository.findByEventId(eventId))
                .thenReturn(List.of(demand));

        when(demand.getDemandId())
                .thenReturn("DMD-123");

        when(reservationRepository.existsByDemandId("DMD-123"))
                .thenReturn(false);

        when(demand.getRequiredPart())
                .thenReturn(part);

        when(part.getPartId())
                .thenReturn("PART-123");

        when(inventoryStockRepository.findByPartId("PART-123"))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> reservationService.reserveMaterialForDemand(eventId)
        );

        assertEquals("INVENTORY_STOCK_NOT_FOUND", exception.getErrorCode());

        verify(reservationRepository, never())
                .persist(any(Reservation.class));

        verifyNoInteractions(shortageEventPublisher);
        verifyNoInteractions(cancelledReservationService);
    }

    @Test
    void shouldHandleShortageAndReturnEmptyListWhenStockIsInsufficient() {
        String eventId = "EVT-123";

        when(materialDemandRepository.findByEventId(eventId))
                .thenReturn(List.of(demand));

        when(demand.getDemandId())
                .thenReturn("DMD-123");

        when(reservationRepository.existsByDemandId("DMD-123"))
                .thenReturn(false);

        when(demand.getRequiredPart())
                .thenReturn(part);

        when(part.getPartId())
                .thenReturn("PART-123");

        when(part.getPartCode())
                .thenReturn("PART-001");

        when(part.getPartName())
                .thenReturn("Test Part");

        when(demand.getVehicleId())
                .thenReturn("VEH-123");

        when(demand.getPlanId())
                .thenReturn("PLAN-123");

        when(demand.getRequiredQuantity())
                .thenReturn(Math.toIntExact(100L));

        when(stock.getAvailableQuantity())
                .thenReturn(40L);

        when(inventoryStockRepository.findByPartId("PART-123"))
                .thenReturn(Optional.of(stock));

        List<ReservationResponse> result = reservationService.reserveMaterialForDemand(eventId);

        assertTrue(result.isEmpty());

        verify(cancelledReservationService)
                .saveCancelledReservation(any(Reservation.class));

        verify(shortageEventPublisher)
                .publishShortage(
                        "VEH-123",
                        "PLAN-123",
                        "PART-123",
                        "Test Part",
                        100,
                        40,
                        60
                );

        verify(stock, never())
                .setAvailableQuantity(anyLong());

        verify(stock, never())
                .setReservedQuantity(anyLong());

        verify(reservationRepository, never())
                .persist(any(Reservation.class));
    }

    @Test
    void shouldSuccessfullyReserveWhenStockIsSufficient() {
        String eventId = "EVT-123";

        when(materialDemandRepository.findByEventId(eventId))
                .thenReturn(List.of(demand));

        when(demand.getDemandId())
                .thenReturn("DMD-123");

        when(reservationRepository.existsByDemandId("DMD-123"))
                .thenReturn(false);

        when(demand.getRequiredPart())
                .thenReturn(part);

        when(part.getPartId())
                .thenReturn("PART-123");

        when(part.getPartCode())
                .thenReturn("PART-001");

        when(demand.getRequiredQuantity())
                .thenReturn(Math.toIntExact(10L));

        when(stock.getAvailableQuantity())
                .thenReturn(50L);

        when(stock.getReservedQuantity())
                .thenReturn(5L);

        when(inventoryStockRepository.findByPartId("PART-123"))
                .thenReturn(Optional.of(stock));

        List<ReservationResponse> result = reservationService.reserveMaterialForDemand(eventId);

        assertEquals(1, result.size());
        verify(stock).setAvailableQuantity(40L);
        verify(stock).setReservedQuantity(15L);
        verify(reservationRepository).persist(any(Reservation.class));
        verifyNoInteractions(shortageEventPublisher);
        verifyNoInteractions(cancelledReservationService);
    }

    @Test
    void shouldConsumeReservationSuccessfully() {
        String reservationId = "RES-001";
        Reservation activeReservation = new Reservation();
        activeReservation.setReservationId(reservationId);
        activeReservation.setStatus(ReservationStatus.ACTIVE);
        activeReservation.setQuantityReserved(5L);
        activeReservation.setPart(part);

        when(part.getPartId()).thenReturn("PART-123");
        when(reservationRepository.findByReservationId(reservationId))
                .thenReturn(Optional.of(activeReservation));

        when(inventoryStockRepository.findByPartId("PART-123"))
                .thenReturn(Optional.of(stock));

        when(stock.getReservedQuantity()).thenReturn(10L);
        when(stock.getAvailableQuantity()).thenReturn(20L);
        when(stock.getMinimumQuantity()).thenReturn(5L);

        ReservationResponse response = reservationService.consumeReservation(reservationId);

        assertNotNull(response);
        assertEquals(ReservationStatus.RELEASED, activeReservation.getStatus());
        verify(stock).setReservedQuantity(5L);
        verify(inventoryStockRepository).persist(stock);
        verify(reservationRepository).persist(activeReservation);
        verifyNoInteractions(lowStockPublisher);
    }

    @Test
    void shouldThrowExceptionWhenConsumingCancelledReservation() {
        String reservationId = "RES-001";
        Reservation cancelledReservation = new Reservation();
        cancelledReservation.setReservationId(reservationId);
        cancelledReservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findByReservationId(reservationId))
                .thenReturn(Optional.of(cancelledReservation));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> reservationService.consumeReservation(reservationId)
        );

        assertEquals("CANNOT_CONSUME_CANCELLED_RESERVATION", exception.getErrorCode());
        verifyNoInteractions(inventoryStockRepository);
    }
}