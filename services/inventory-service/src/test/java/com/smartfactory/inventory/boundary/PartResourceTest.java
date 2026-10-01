package com.smartfactory.inventory.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.smartfactory.common.dto.inventory.CreatePartRequest;
import com.smartfactory.common.dto.inventory.PartResponse;
import com.smartfactory.common.enums.UnitOfMeasure;
import com.smartfactory.inventory.control.PartService;
import com.smartfactory.inventory.control.exception.PartAlreadyExistsException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartResourceTest {

    @Mock
    private PartService partService;

    @InjectMocks
    private PartResource partResource;

    @Test
    void shouldCreatePartSuccessfully() {
        final UUID id = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();
        final CreatePartRequest
                request = new CreatePartRequest("PART-001", "MOTOR_V8", "V8 Engine Block", "Engine block description", UnitOfMeasure.PCS, true);
        final PartResponse mockResponse = new PartResponse(id, "PART-001", "MOTOR_V8", "V8 Engine Block", "Engine block description", UnitOfMeasure.PCS, true, now);

        when(partService.createPart(request)).thenReturn(mockResponse);

        final Response response = partResource.createPart(request);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(mockResponse, response.getEntity());
        verify(partService).createPart(request);
    }

    @Test
    void shouldPropagateConflictExceptionWhenPartCodeIsDuplicate() {
        final CreatePartRequest request = new CreatePartRequest("PART-001", "MOTOR_V8", "Duplicate Engine", "Desc", UnitOfMeasure.PCS, true);

        when(partService.createPart(request)).thenThrow(new PartAlreadyExistsException("Part already exists"));

        assertThrows(PartAlreadyExistsException.class, () -> partResource.createPart(request));

        verify(partService).createPart(request);
    }

    @Test
    void shouldGetAllPartsSuccessfully() {
        final UUID id1 = UUID.randomUUID();
        final UUID id2 = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        final PartResponse part1 = new PartResponse(id1, "PART-001", "PART_1", "Part One", "Desc 1", UnitOfMeasure.PCS, true, now);
        final PartResponse part2 = new PartResponse(id2, "PART-002", "PART_2", "Part Two", "Desc 2", UnitOfMeasure.PCS, true, now);
        when(partService.getAllParts()).thenReturn(List.of(part1, part2));

        final Response response = partResource.getAllParts();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked") final List<PartResponse> parts = (List<PartResponse>) response.getEntity();
        assertEquals(2, parts.size());

        verify(partService).getAllParts();
    }

    @Test
    void shouldGetPartByIdSuccessfully() {
        final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        final LocalDateTime now = LocalDateTime.now();
        final PartResponse mockResponse = new PartResponse(id, "PART-001", "PART_1", "Part One", "Desc 1", UnitOfMeasure.PCS, true, now);

        when(partService.getPartById(id)).thenReturn(mockResponse);

        final Response response = partResource.getPartById(id);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        final PartResponse entity = (PartResponse) response.getEntity();
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("PART-001", entity.getPartId());
        assertEquals("PART_1", entity.getPartCode());
        assertEquals("Part One", entity.getPartName());
        assertEquals(UnitOfMeasure.PCS, entity.getUnitOfMeasure());
        verify(partService).getPartById(id);
    }

    @Test
    void shouldPropagateNotFoundExceptionWhenPartDoesNotExist() {
        final UUID nonExistentId = UUID.fromString("99999999-9999-9999-9999-999999999999");

        when(partService.getPartById(nonExistentId)).thenThrow(new NotFoundException("Part not found"));

        assertThrows(NotFoundException.class, () -> partResource.getPartById(nonExistentId));
        verify(partService).getPartById(nonExistentId);
    }

    @Test
    void shouldGetPartByCodeSuccessfully() {
        final UUID id = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();
        final PartResponse mockResponse = new PartResponse(id, "PART-001", "PART_1", "Part One", "Desc 1", UnitOfMeasure.PCS, true, now);

        when(partService.getPartByCode("PART_1")).thenReturn(mockResponse);

        final Response result = partResource.getPartByCode("PART_1");

        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());

        final PartResponse entity = (PartResponse) result.getEntity();
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("PART-001", entity.getPartId());
        assertEquals("PART_1", entity.getPartCode());
        assertEquals("Part One", entity.getPartName());
        assertEquals(UnitOfMeasure.PCS, entity.getUnitOfMeasure());
        verify(partService).getPartByCode("PART_1");
    }

    @Test
    void shouldPropagateNotFoundExceptionWhenPartCodeDoesNotExist() {
        when(partService.getPartByCode("NON_EXISTENT_CODE")).thenThrow(new NotFoundException("Part not found"));

        assertThrows(NotFoundException.class, () -> partResource.getPartByCode("NON_EXISTENT_CODE"));
        verify(partService).getPartByCode("NON_EXISTENT_CODE");
    }
}