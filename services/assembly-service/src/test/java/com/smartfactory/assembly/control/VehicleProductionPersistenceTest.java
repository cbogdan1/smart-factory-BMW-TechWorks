package com.smartfactory.assembly.control;

import static com.smartfactory.common.enums.ProductionStage.BODY;
import static com.smartfactory.common.enums.ProductionStage.CREATED;
import static com.smartfactory.common.enums.ProductionStatus.IN_PRODUCTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneOffset;
import java.util.Optional;
import java.util.List;

import com.smartfactory.assembly.control.repositories.VehicleProductionRepository;
import com.smartfactory.assembly.control.services.VehicleProductionService;
import com.smartfactory.assembly.entity.VehicleProduction;
import com.smartfactory.common.enums.ProductionStatus;
import com.smartfactory.common.payloads.inventory_service.PartsReservedPayload;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

@QuarkusTest
class VehicleProductionPersistenceTest {

    @Inject
    VehicleProductionRepository repository;

    @Inject
    VehicleProductionService service;

    @Inject
    EntityManager entityManager;

    @Test
    @TestTransaction
    void shouldPersistStartedAtWhenProductionStarts() {
        final String vehicleId = "VEH-DB-001";
        final String eventId = "EVT-DB-001";
        final PartsReservedPayload payload = new PartsReservedPayload("RES-DB-001", vehicleId, "PLAN-DB-001",
                List.of(), "2026-08-15T10:00:00Z");

        final VehicleProduction vehicleProduction = new VehicleProduction();
        vehicleProduction.setVehicleId(vehicleId);
        vehicleProduction.setOrderId("ORD-DB-001");
        vehicleProduction.setVehicleModel("BMW_I4");
        vehicleProduction.setProductionLine("LINE-2");
        vehicleProduction.setCurrentStage(CREATED);
        vehicleProduction.setStatus(ProductionStatus.CREATED);
        vehicleProduction.setStartedAt(null);
        vehicleProduction.setStartedEventId(null);
        vehicleProduction.setCompletedAt(null);

        repository.persist(vehicleProduction);
        repository.flush();
        entityManager.clear();

        service.startProduction(eventId, payload);

        repository.flush();
        entityManager.clear();

        final Optional<VehicleProduction> persistedResult =
                repository.findByIdOptional(vehicleId);

        assertTrue(persistedResult.isPresent());

        final VehicleProduction persistedVehicle = persistedResult.get();

        assertEquals("BMW_I4", persistedVehicle.getVehicleModel());
        assertEquals("LINE-2", persistedVehicle.getProductionLine());
        assertEquals(IN_PRODUCTION, persistedVehicle.getStatus());
        assertEquals(BODY, persistedVehicle.getCurrentStage());
        assertNotNull(persistedVehicle.getStartedAt());
        assertEquals(ZoneOffset.UTC, persistedVehicle.getStartedAt().getOffset());
        assertEquals(eventId, persistedVehicle.getStartedEventId());
    }
}