package com.smartfactory.inventory.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.smartfactory.common.dto.inventory.CreatePartRequest;
import com.smartfactory.common.dto.inventory.PartResponse;
import com.smartfactory.common.enums.UnitOfMeasure;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.inventory.control.mapper.PartMapper;
import com.smartfactory.inventory.entity.Part;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartServiceTest {

    @Mock
    private PartRepository partRepository;

    @Mock
    private PartMapper partMapper;

    @InjectMocks
    private PartService partService;

    private static Part createPart(
            final UUID id,
            final String partId,
            final String partCode,
            final String partName,
            final String description,
            final boolean active,
            final LocalDateTime createdDate) {

        final Part part = new Part();
        part.setId(id);
        part.setPartId(partId);
        part.setPartCode(partCode);
        part.setPartName(partName);
        part.setDescription(description);
        part.setUnitOfMeasure(UnitOfMeasure.PCS);
        part.setActive(active);
        part.setCreatedDate(createdDate);
        return part;
    }

    private static PartResponse createPartResponse(
            final UUID id,
            final String partId,
            final String partCode,
            final String partName,
            final String description,
            final LocalDateTime createdDate) {

        return new PartResponse(
                id,
                partId,
                partCode,
                partName,
                description,
                UnitOfMeasure.PCS,
                true,
                createdDate
        );
    }

    private void assertPartResponseFields(
            final PartResponse response,
            final UUID expectedId,
            final String expectedPartId,
            final String expectedPartCode,
            final String expectedPartName) {

        assertNotNull(response);
        assertEquals(expectedId, response.getId());
        assertEquals(expectedPartId, response.getPartId());
        assertEquals(expectedPartCode, response.getPartCode());
        assertEquals(expectedPartName, response.getPartName());
        assertEquals(UnitOfMeasure.PCS, response.getUnitOfMeasure());
    }


    @Test
    void shouldCreatePartSuccessfully() {

        final CreatePartRequest request =
                new CreatePartRequest(
                        "PART-001",
                        "MOTOR_V8",
                        "V8 Engine Block",
                        "Engine block description",
                        UnitOfMeasure.PCS,
                        true
                );

        final UUID generatedId = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        final Part part =
                createPart(
                        generatedId,
                        "PART-001",
                        "MOTOR_V8",
                        "V8 Engine Block",
                        "Engine block description",
                        true,
                        now
                );

        final PartResponse expectedResponse =
                createPartResponse(
                        generatedId,
                        "PART-001",
                        "MOTOR_V8",
                        "V8 Engine Block",
                        "Engine block description",
                        now
                );

        when(partRepository.findByPartCode("MOTOR_V8"))
                .thenReturn(Optional.empty());

        when(partMapper.toEntity(request))
                .thenReturn(part);

        when(partMapper.toResponse(part))
                .thenReturn(expectedResponse);

        final PartResponse response =
                partService.createPart(request);

        assertPartResponseFields(
                response,
                generatedId,
                "PART-001",
                "MOTOR_V8",
                "V8 Engine Block"
        );

        assertEquals(
                "Engine block description",
                response.getDescription()
        );

        verify(partRepository)
                .persist(any(Part.class));
    }

    @Test
    void shouldThrowExceptionWhenPartCodeIsDuplicate() {

        final CreatePartRequest request =
                new CreatePartRequest(
                        "PART-001",
                        "MOTOR_V8",
                        "Duplicate Engine",
                        "Desc",
                        UnitOfMeasure.PCS,
                        true
                );

        final Part existingPart =
                createPart(
                        UUID.randomUUID(),
                        "PART-002",
                        "MOTOR_V8",
                        "V8 Engine Block",
                        null,
                        false,
                        LocalDateTime.now()
                );

        when(partRepository.findByPartCode("MOTOR_V8"))
                .thenReturn(Optional.of(existingPart));

        final BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> partService.createPart(request)
                );

        assertEquals(
                "PART_ALREADY_EXISTS",
                exception.getErrorCode()
        );

        verify(partRepository, never())
                .persist(any(Part.class));
    }


    @Test
    void shouldGetAllPartsSuccessfully() {

        final UUID id1 = UUID.randomUUID();
        final UUID id2 = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        final Part part1 =
                createPart(
                        id1,
                        "PART-001",
                        "PART_1",
                        "Part One",
                        "Desc 1",
                        true,
                        now
                );

        final Part part2 =
                createPart(
                        id2,
                        "PART-002",
                        "PART_2",
                        "Part Two",
                        "Desc 2",
                        true,
                        now
                );

        when(partRepository.listAll())
                .thenReturn(List.of(part1, part2));

        final PartResponse res1 =
                createPartResponse(
                        id1,
                        "PART-001",
                        "PART_1",
                        "Part One",
                        "Desc 1",
                        now
                );

        final PartResponse res2 =
                createPartResponse(
                        id2,
                        "PART-002",
                        "PART_2",
                        "Part Two",
                        "Desc 2",
                        now
                );

        when(partMapper.toResponse(part1))
                .thenReturn(res1);

        when(partMapper.toResponse(part2))
                .thenReturn(res2);

        final List<PartResponse> response =
                partService.getAllParts();

        assertEquals(2, response.size());
        assertEquals("PART_1", response.get(0).getPartCode());
        assertEquals("PART_2", response.get(1).getPartCode());
    }

    @Test
    void shouldGetPartByIdSuccessfully() {

        final UUID partId = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        final Part part =
                createPart(
                        partId,
                        "PART-001",
                        "PART_1",
                        "Part One",
                        "Desc 1",
                        true,
                        now
                );

        final PartResponse expectedResponse =
                createPartResponse(
                        partId,
                        "PART-001",
                        "PART_1",
                        "Part One",
                        "Desc 1",
                        now
                );

        when(partRepository.findByIdOptional(partId))
                .thenReturn(Optional.of(part));

        when(partMapper.toResponse(part))
                .thenReturn(expectedResponse);

        final PartResponse response =
                partService.getPartById(partId);

        assertPartResponseFields(
                response,
                partId,
                "PART-001",
                "PART_1",
                "Part One"
        );
    }

    @Test
    void shouldThrowBusinessExceptionWhenPartDoesNotExist() {

        final UUID nonExistentId = UUID.randomUUID();

        when(partRepository.findByIdOptional(nonExistentId))
                .thenReturn(Optional.empty());

        final BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> partService.getPartById(nonExistentId)
                );

        assertEquals(
                "PART_NOT_FOUND",
                exception.getErrorCode()
        );
    }

    @Test
    void shouldGetPartByCodeSuccessfully() {

        final UUID partId = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        final Part part =
                createPart(
                        partId,
                        "PART-001",
                        "PART_1",
                        "Part One",
                        "Desc 1",
                        true,
                        now
                );

        final PartResponse expectedResponse =
                createPartResponse(
                        partId,
                        "PART-001",
                        "PART_1",
                        "Part One",
                        "Desc 1",
                        now
                );

        when(partRepository.findByPartCode("PART_1"))
                .thenReturn(Optional.of(part));

        when(partMapper.toResponse(part))
                .thenReturn(expectedResponse);

        final PartResponse response =
                partService.getPartByCode("PART_1");

        assertPartResponseFields(
                response,
                partId,
                "PART-001",
                "PART_1",
                "Part One"
        );
    }

    @Test
    void shouldThrowBusinessExceptionWhenPartCodeDoesNotExist() {

        when(partRepository.findByPartCode("NON_EXISTENT_CODE"))
                .thenReturn(Optional.empty());

        final BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> partService.getPartByCode("NON_EXISTENT_CODE")
                );

        assertEquals(
                "PART_NOT_FOUND",
                exception.getErrorCode()
        );
    }

    @Test
    void shouldGetPartByPartIdSuccessfully() {

        final UUID generatedId = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        final Part part =
                createPart(
                        generatedId,
                        "PART-001",
                        "PART_1",
                        "Part One",
                        "Desc 1",
                        true,
                        now
                );

        final PartResponse expectedResponse =
                createPartResponse(
                        generatedId,
                        "PART-001",
                        "PART_1",
                        "Part One",
                        "Desc 1",
                        now
                );

        when(partRepository.findByPartId("PART-001"))
                .thenReturn(Optional.of(part));

        when(partMapper.toResponse(part))
                .thenReturn(expectedResponse);

        final PartResponse response =
                partService.getPartByPartId("PART-001");

        assertPartResponseFields(
                response,
                generatedId,
                "PART-001",
                "PART_1",
                "Part One"
        );
    }

    @Test
    void shouldThrowBusinessExceptionWhenPartIdDoesNotExist() {

        when(partRepository.findByPartId("NON_EXISTENT_PART_ID"))
                .thenReturn(Optional.empty());

        final BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> partService.getPartByPartId(
                                "NON_EXISTENT_PART_ID"
                        )
                );

        assertEquals(
                "PART_NOT_FOUND",
                exception.getErrorCode()
        );
    }
}