package com.smartfactory.inventory.control;

import static com.smartfactory.common.exception.InventoryServiceExceptions.CANNOT_CONSUME_CANCELLED_RESERVATION_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.CANNOT_CONSUME_CANCELLED_RESERVATION_ERROR_MESSAGE;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.smartfactory.common.dto.inventory.ReservationResponse;
import com.smartfactory.common.enums.ReservationStatus;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;
import com.smartfactory.inventory.boundary.LowStockPublisher;
import com.smartfactory.inventory.boundary.ShortageEventPublisher;
import com.smartfactory.inventory.control.exception.InventoryStockNotFoundException;
import com.smartfactory.inventory.control.exception.MaterialDemandsNotFoundException;
import com.smartfactory.inventory.control.exception.ReservationNotFoundException;
import com.smartfactory.inventory.control.mapper.ReservationMapper;
import com.smartfactory.inventory.entity.InventoryStock;
import com.smartfactory.inventory.entity.MaterialDemand;
import com.smartfactory.inventory.entity.Reservation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ReservationService {

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    MaterialDemandRepository materialDemandRepository;

    @Inject
    InventoryStockRepository inventoryStockRepository;

    @Inject
    ShortageEventPublisher shortageEventPublisher;
    @Inject
    CancelledReservationService cancelledReservationService;
    @Inject
    LowStockPublisher lowStockPublisher;

    /**
     * Reserve materials for a demand
     *
     * @param eventId the id of the Kafka event
     * @return list of material reservations made
     */
    @Transactional
    public List<ReservationResponse> reserveMaterialForDemand(final String eventId) {
        final List<MaterialDemand> demands = materialDemandRepository.findByEventId(eventId);

        if (demands.isEmpty()) {
            throw new MaterialDemandsNotFoundException(eventId);
        }

        final List<ReservationResponse> responses = new ArrayList<>();

        boolean isShortage = false;
        for (final MaterialDemand demand : demands) {

            // Check if this particular demand was already reserved
            if (reservationRepository.existsByDemandId(demand.getDemandId())) {

                log.warn("Reservation already exists for demand ID {} (event ID {}). Skipping.", demand.getDemandId(),
                        eventId);

                final Reservation existing = reservationRepository.findByDemandId(demand.getDemandId())
                        .orElseThrow(() -> new ReservationNotFoundException(demand.getDemandId()));

                final ReservationResponse response = ReservationMapper.toResponse(existing);

                responses.add(response);

                continue;
            }

            // Find inventory for this particular part
            final InventoryStock stock = inventoryStockRepository.findByPartId(demand.getRequiredPart().getPartId())
                    .orElseThrow(() -> new InventoryStockNotFoundException(demand.getRequiredPart().getId()));

            final long requiredQuantity = demand.getRequiredQuantity();

            final long availableQuantity = stock.getAvailableQuantity();

            // Check inventory
            if (availableQuantity < requiredQuantity) {
                final long missingQuantity = requiredQuantity - availableQuantity;

                log.error("Insufficient stock for part {}. Required: {}, Available: {}, Missing: {}",
                        demand.getRequiredPart().getPartCode(), requiredQuantity, availableQuantity, missingQuantity);

                // Create a cancelled reservation
                final Reservation reservation = ReservationMapper.cancelledFromDemand(demand);

                // Persist the cancelled reservations as well through a separate service
                // all while keeping their staus in the db as CANCELLED.
                // Why the separate service? Because throwing an error here
                // due to insufficient stock would cause the transaction to rollback
                cancelledReservationService.saveCancelledReservation(reservation);

                // Notify Procurement about the shortage.
                shortageEventPublisher.publishShortage(demand.getVehicleId(), demand.getPlanId(),
                        demand.getRequiredPart().getPartId(), demand.getRequiredPart().getPartName(),
                        Math.toIntExact(requiredQuantity), Math.toIntExact(availableQuantity),
                        Math.toIntExact(missingQuantity));

                isShortage = true;

                continue;
            }

            // Update inventory
            stock.setAvailableQuantity(stock.getAvailableQuantity() - requiredQuantity);

            stock.setReservedQuantity(stock.getReservedQuantity() + requiredQuantity);

            // Create reservation
            final Reservation reservation = new Reservation();

            reservation.setReservationId("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            reservation.setMaterialDemand(demand);
            reservation.setPart(demand.getRequiredPart());
            reservation.setQuantityReserved(requiredQuantity);
            reservation.setStatus(ReservationStatus.ACTIVE);
            reservation.setReservedAt(OffsetDateTime.now());

            reservationRepository.persist(reservation);

            log.info("Successfully reserved {} units of part {} (Reservation ID: {})", requiredQuantity,
                    demand.getRequiredPart().getPartCode(), reservation.getReservationId());

            final ReservationResponse response = ReservationMapper.toResponse(reservation);

            responses.add(response);

        }

        if (isShortage) {
            log.warn("Insufficient stock while processing production-planned event {}. Shortage events sent.", eventId);
            return Collections.emptyList();
        }

        return responses;
    }

    /**
     * Get reservations by vehicle id
     * @param vehicleId the id of the vehicle
     * @return a list of all material reservations for said vehicle
     */
    public List<ReservationResponse> getReservationsByVehicleId(final String vehicleId) {
        final List<Reservation> reservations = reservationRepository.findByVehicleId(vehicleId);
        return reservations.stream().map(ReservationMapper::toResponse).toList();
    }

    @Transactional
    public ReservationResponse consumeReservation(final String reservationId) {
        final Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + reservationId));

        if (reservation.getStatus() == ReservationStatus.RELEASED) {
            log.info("Reservation {} is already RELEASED. Skipping", reservationId);
            return ReservationMapper.toResponse(reservation);
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException(
                    String.format(
                            CANNOT_CONSUME_CANCELLED_RESERVATION_ERROR_MESSAGE,
                            reservationId,
                            reservation.getStatus()
                    ),
                    CANNOT_CONSUME_CANCELLED_RESERVATION_ERROR_CODE,
                    400
            );
        }

        final InventoryStock stock = inventoryStockRepository.findByPartId(reservation.getPart().getPartId())
                .orElseThrow(() -> new InventoryStockNotFoundException(
                        "Inventory stock not found: " + reservation.getPart().getPartId()));

        final long newReserved = Math.max(0, stock.getReservedQuantity() - reservation.getQuantityReserved());
        stock.setReservedQuantity(newReserved);
        inventoryStockRepository.persist(stock);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservationRepository.persist(reservation);

        log.info("Reservation {} consumed. Reserved quantity updated to {} for part {}", reservationId, newReserved,
                reservation.getPart().getPartId());

        if (stock.getAvailableQuantity() < stock.getMinimumQuantity()) {
            lowStockPublisher.publishLowStock(stock.getPart().getPartId(), stock.getPart().getPartCode(),
                    stock.getAvailableQuantity(), stock.getMinimumQuantity());
        }
        return ReservationMapper.toResponse(reservation);
    }

    @Transactional
    public void processMaterialDelivered(final MaterialDeliveredPayload payload) {
        final List<Reservation> reservations = reservationRepository.findByVehicleId(payload.vehicleId());

        if (reservations.isEmpty()) {
            log.warn("No reservations found for vehicleId={} on material-delivered event", payload.vehicleId());
            return;
        }

        final List<Reservation> activeReservations =
                reservations.stream().filter(r -> r.getStatus() == ReservationStatus.ACTIVE)
                        .filter(r -> r.getPart().getPartId().equals(payload.material()) || r.getPart().getPartCode()
                                .equals(payload.material())).toList();

        if (activeReservations.isEmpty()) {
            log.info("No active reservations to consume for vehicleId={} and material={}", payload.vehicleId(),
                    payload.material());
            return;
        }

        for (final Reservation reservation : activeReservations) {
            consumeReservation(reservation.getReservationId());
        }

        log.info("Successfully processed material delivery from mission {} for vehicle {}",
                payload.missionId(), payload.vehicleId());
    }

}