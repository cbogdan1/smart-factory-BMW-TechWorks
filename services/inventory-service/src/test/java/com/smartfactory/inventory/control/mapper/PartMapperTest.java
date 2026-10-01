package com.smartfactory.inventory.control.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import com.smartfactory.common.dto.inventory.CreatePartRequest;
import com.smartfactory.common.dto.inventory.PartResponse;
import com.smartfactory.common.enums.UnitOfMeasure;
import com.smartfactory.inventory.entity.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartMapperTest {

    private PartMapper partMapper;

    @BeforeEach
    void setUp() {
        partMapper = new PartMapper();
    }

    @Test
    void shouldMapToEntity() {
        final CreatePartRequest request =
                new CreatePartRequest("PART-001", "P-CODE", "Engine", "V8", UnitOfMeasure.PCS, true);

        final Part part = partMapper.toEntity(request);

        assertEquals("PART-001", part.getPartId());
        assertEquals("P-CODE", part.getPartCode());
        assertEquals("Engine", part.getPartName());
        assertEquals("V8", part.getDescription());
        assertEquals(UnitOfMeasure.PCS, part.getUnitOfMeasure());
        assertTrue(part.getActive());
    }

    @Test
    void shouldReturnNullWhenMappingNullRequest() {
        assertNull(partMapper.toEntity(null));
    }

    @Test
    void shouldMapToResponse() {
        final Part part = new Part();
        part.setId(UUID.randomUUID());
        part.setPartId("PART-001");
        part.setPartCode("CODE");
        part.setPartName("Name");
        part.setDescription("Desc");
        part.setUnitOfMeasure(UnitOfMeasure.PCS);
        part.setActive(true);
        part.setCreatedDate(LocalDateTime.now());

        final PartResponse response = partMapper.toResponse(part);

        assertEquals(part.getId(), response.getId());
        assertEquals(part.getPartId(), response.getPartId());
        assertEquals(part.getPartCode(), response.getPartCode());
    }

    @Test
    void shouldReturnNullWhenMappingNullEntity() {
        assertNull(partMapper.toResponse(null));
    }
}