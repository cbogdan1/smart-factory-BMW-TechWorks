package com.smartfactory.inventory.control.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.smartfactory.common.dto.inventory.MaterialDemandResponse;
import com.smartfactory.common.enums.DemandStatus;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.inventory.entity.MaterialDemand;
import com.smartfactory.inventory.entity.Part;
import com.smartfactory.inventory.entity.VehicleBom;
import org.junit.jupiter.api.Test;

class MaterialDemandMapperTest {

    @Test
    void shouldMapToEntityFromResponse() {
        Part part = new Part();
        part.setPartCode("PART-123");

        MaterialDemandResponse response = new MaterialDemandResponse();
        response.setId(UUID.randomUUID());
        response.setDemandId("DMD-1");
        response.setPlanId("PLAN-1");
        response.setVehicleId("VEH-1");
        response.setRequiredQuantity(5);
        response.setPlannedDate(LocalDate.now());
        response.setStatus(DemandStatus.PLANNED);
        response.setCreatedDate(LocalDateTime.now());
        response.setVehicleModel("MODEL-X");
        response.setEventId("EVT-1");

        MaterialDemand result = MaterialDemandMapper.toEntity(response, part);

        assertEquals(response.getId(), result.getId());
        assertEquals("DMD-1", result.getDemandId());
        assertEquals("PART-123", result.getRequiredPart().getPartCode());
    }

    @Test
    void shouldReturnNullWhenMappingNullResponse() {
        assertNull(MaterialDemandMapper.toEntity((MaterialDemandResponse) null, new Part()));
    }

    @Test
    void shouldMapToEntityFromPayloadAndBom() {
        ProductionPlannedPayload payload = new ProductionPlannedPayload(
                "PLAN-1",
                "ORD-1",
                "VEH-1",
                "MODEL-X",
                "LINE-1",
                Priority.HIGH,
                LocalDate.now()
        );

        VehicleBom bom = new VehicleBom();
        Part part = new Part();
        part.setPartCode("PART-001");
        bom.setPart(part);
        bom.setQuantity(4);

        MaterialDemand result = MaterialDemandMapper.toEntity(payload, "EVT-123", bom);

        assertNotNull(result.getDemandId());
        assertEquals("PLAN-1", result.getPlanId());
        assertEquals("VEH-1", result.getVehicleId());
        assertEquals(4, result.getRequiredQuantity());
        assertEquals("MODEL-X", result.getVehicleModel());
        assertEquals("EVT-123", result.getEventId());
    }

    @Test
    void shouldMapFromEntity() {
        Part part = new Part();
        part.setPartCode("PART-CODE");

        MaterialDemand demand = new MaterialDemand();
        demand.setId(UUID.randomUUID());
        demand.setDemandId("DMD-001");
        demand.setPlanId("PLAN-001");
        demand.setVehicleId("VEH-001");
        demand.setRequiredPart(part);
        demand.setRequiredQuantity(10);
        demand.setPlannedDate(LocalDate.now());
        demand.setStatus(DemandStatus.PLANNED);
        demand.setCreatedDate(LocalDateTime.now());
        demand.setVehicleModel("SUV");
        demand.setEventId("EVT-001");

        MaterialDemandResponse result = MaterialDemandMapper.fromEntity(demand);

        assertEquals(demand.getId(), result.getId());
        assertEquals("DMD-001", result.getDemandId());
        assertEquals("PART-CODE", result.getRequiredPartCode());
    }
}