package com.smartfactory.quality.control.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.smartfactory.common.Topics;
import com.smartfactory.common.dto.quality.AddDefectToInspectionRequest;
import com.smartfactory.common.dto.quality.CreateInspectionRequest;
import com.smartfactory.common.dto.quality.InspectionDecisionResponse;
import com.smartfactory.common.dto.quality.InspectionDefectResponse;
import com.smartfactory.common.dto.quality.InspectionDefectView;
import com.smartfactory.common.dto.quality.InspectionResponse;
import com.smartfactory.common.dto.quality.InspectionScoreResponse;
import com.smartfactory.common.dto.quality.InspectionView;
import com.smartfactory.common.enums.Decision;
import com.smartfactory.common.enums.InspectionStatus;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;
import com.smartfactory.common.payloads.quality_service.QualityFailedPayload;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import com.smartfactory.quality.control.repositories.DefectRepository;
import com.smartfactory.quality.control.repositories.InspectionDefectRepository;
import com.smartfactory.quality.control.repositories.InspectionRepository;
import com.smartfactory.quality.control.repositories.ProcessedEventRepository;
import com.smartfactory.quality.entity.Defect;
import com.smartfactory.quality.entity.Inspection;
import com.smartfactory.quality.entity.InspectionDefect;
import com.smartfactory.quality.entity.ProcessedEvent;
import com.smartfactory.quality.entity.engine.DefectSeeder;
import com.smartfactory.quality.entity.engine.InspectionDecisionEngine;
import jakarta.enterprise.event.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InspectionServiceTest {

    @Mock
    InspectionRepository inspectionRepository;

    @Mock
    InspectionDefectRepository inspectionDefectRepository;

    @Mock
    DefectRepository defectRepository;

    @Mock
    ProcessedEventRepository processedEventRepository;

    @Mock
    DefectSeeder defectSeeder;

    @InjectMocks
    InspectionService inspectionService;

    @Spy
    InspectionDecisionEngine inspectionDecisionEngine = new InspectionDecisionEngine();

    @Mock
    Event<QualityApprovedPayload> approvedEvent;

    @Mock
    Event<QualityReworkRequiredPayload> reworkRequiredEvent;

    @Mock
    Event<QualityFailedPayload> failedEvent;

    @Test
    public void createInspection() {
        final CreateInspectionRequest request = new CreateInspectionRequest();
        request.setVehicleId("VEH-001");

        final InspectionResponse response = inspectionService.createInspection(request);

        assertNotNull(response);
        assertNotNull(response.getInspectionId());
        assertTrue(response.getInspectionId().startsWith("INSP-"));
        assertEquals("VEH-001", response.getVehicleId());
        assertNotNull(response.getInspectionDate());
        assertEquals(InspectionStatus.PENDING, response.getStatus());

        verify(inspectionRepository).persist(any(Inspection.class));
    }

    @Test
    public void findById() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-002");
        inspection.setVehicleId("VEH-002");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        when(inspectionRepository.findById("INSP-002")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-002")).thenReturn(new ArrayList<>());

        final InspectionView view = inspectionService.findById("INSP-002");

        assertNotNull(view);
        assertEquals("INSP-002", view.getInspectionId());
        assertEquals("VEH-002", view.getVehicleId());
        assertNotNull(view.getInspectionDate());
        assertEquals(InspectionStatus.PENDING, view.getStatus());
        assertNotNull(view.getDefects());
        assertTrue(view.getDefects().isEmpty());
    }

    @Test
    public void findByIdWithDefects() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-008");
        inspection.setVehicleId("VEH-008");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.REWORK_REQUIRED);

        final InspectionDefect firstLink = new InspectionDefect();
        firstLink.setInspectionDefectId("INSPDEF-001");
        firstLink.setInspectionId("INSP-008");
        firstLink.setDefectCode("PAINT_SCRATCH");
        firstLink.setComment("Scratch on the left door");
        firstLink.setCreatedDate(LocalDateTime.now());

        final InspectionDefect secondLink = new InspectionDefect();
        secondLink.setInspectionDefectId("INSPDEF-002");
        secondLink.setInspectionId("INSP-008");
        secondLink.setDefectCode("MISSING_SEAT");
        secondLink.setCreatedDate(LocalDateTime.now());

        final Defect paintScratch = new Defect();
        paintScratch.setCode("PAINT_SCRATCH");
        paintScratch.setDescription("Paint scratch detected");
        paintScratch.setPenaltyPoints(5);
        paintScratch.setActive(true);
        paintScratch.setCreatedDate(LocalDateTime.now());

        final Defect missingSeat = new Defect();
        missingSeat.setCode("MISSING_SEAT");
        missingSeat.setDescription("Seat is missing");
        missingSeat.setPenaltyPoints(20);
        missingSeat.setActive(true);
        missingSeat.setCreatedDate(LocalDateTime.now());

        when(inspectionRepository.findById("INSP-008")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-008")).thenReturn(List.of(firstLink, secondLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(paintScratch, missingSeat));

        final InspectionView view = inspectionService.findById("INSP-008");

        assertNotNull(view);
        assertEquals("INSP-008", view.getInspectionId());
        assertEquals("VEH-008", view.getVehicleId());
        assertEquals(InspectionStatus.REWORK_REQUIRED, view.getStatus());
        assertEquals(2, view.getDefects().size());

        final InspectionDefectView firstDefect = view.getDefects().get(0);
        assertEquals("PAINT_SCRATCH", firstDefect.getDefectCode());
        assertEquals("Paint scratch detected", firstDefect.getDescription());
        assertEquals(5, firstDefect.getPenaltyPoints());
        assertEquals("Scratch on the left door", firstDefect.getComment());

        final InspectionDefectView secondDefect = view.getDefects().get(1);
        assertEquals("MISSING_SEAT", secondDefect.getDefectCode());
        assertEquals("Seat is missing", secondDefect.getDescription());
        assertEquals(20, secondDefect.getPenaltyPoints());
        assertNull(secondDefect.getComment());
    }

    @Test
    public void findByIdKeepsDefectsInTheOrderReturnedByTheRepository() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-009");
        inspection.setVehicleId("VEH-009");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.FAILED);

        final InspectionDefect firstLink = new InspectionDefect();
        firstLink.setInspectionDefectId("INSPDEF-003");
        firstLink.setInspectionId("INSP-009");
        firstLink.setDefectCode("DOOR_ALIGNMENT");
        firstLink.setCreatedDate(LocalDateTime.now());

        final InspectionDefect secondLink = new InspectionDefect();
        secondLink.setInspectionDefectId("INSPDEF-004");
        secondLink.setInspectionId("INSP-009");
        secondLink.setDefectCode("PAINT_SCRATCH");
        secondLink.setCreatedDate(LocalDateTime.now());

        final Defect doorAlignment = new Defect();
        doorAlignment.setCode("DOOR_ALIGNMENT");
        doorAlignment.setDescription("Door alignment issue");
        doorAlignment.setPenaltyPoints(8);

        final Defect paintScratch = new Defect();
        paintScratch.setCode("PAINT_SCRATCH");
        paintScratch.setDescription("Paint scratch detected");
        paintScratch.setPenaltyPoints(5);

        when(inspectionRepository.findById("INSP-009")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-009")).thenReturn(List.of(firstLink, secondLink));
        // The defect lookup is a single batch query, so the database may return the defects in any order.
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(paintScratch, doorAlignment));

        final InspectionView view = inspectionService.findById("INSP-009");

        assertEquals(2, view.getDefects().size());
        assertEquals("DOOR_ALIGNMENT", view.getDefects().get(0).getDefectCode());
        assertEquals("Door alignment issue", view.getDefects().get(0).getDescription());
        assertEquals("PAINT_SCRATCH", view.getDefects().get(1).getDefectCode());
        assertEquals("Paint scratch detected", view.getDefects().get(1).getDescription());
    }

    @Test
    public void findByIdNotFound() {
        when(inspectionRepository.findById("INSP-999")).thenReturn(null);

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> inspectionService.findById("INSP-999"));

        assertEquals("INSPECTION_NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
        assertEquals("Inspection 'INSP-999' not found", exception.getMessage());
        verifyNoInteractions(inspectionDefectRepository);
    }

    @Test
    public void findAll() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-003");
        inspection.setVehicleId("VEH-003");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final List<Inspection> inspections = new ArrayList<>();
        inspections.add(inspection);

        when(inspectionRepository.listAll()).thenReturn(inspections);

        final List<InspectionResponse> responses = inspectionService.findAll();

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("INSP-003", responses.get(0).getInspectionId());
        assertEquals("VEH-003", responses.get(0).getVehicleId());
        assertEquals(InspectionStatus.PENDING, responses.get(0).getStatus());
    }

    @Test
    public void addDefectToInspection() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-004");
        inspection.setVehicleId("VEH-004");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final Defect defect = new Defect();
        defect.setCode("PAINT_SCRATCH");
        defect.setDescription("Paint scratch detected");
        defect.setPenaltyPoints(5);
        defect.setActive(true);
        defect.setCreatedDate(LocalDateTime.now());

        final AddDefectToInspectionRequest request = new AddDefectToInspectionRequest();
        request.setDefectCode("PAINT_SCRATCH");
        request.setComment("Scratch on the left door");

        when(inspectionRepository.findById("INSP-004")).thenReturn(inspection);
        when(defectRepository.findById("PAINT_SCRATCH")).thenReturn(defect);
        when(inspectionDefectRepository.existsByInspectionIdAndDefectCode("INSP-004", "PAINT_SCRATCH")).thenReturn(
                false);

        final InspectionDefectResponse response = inspectionService.addDefectToInspection("INSP-004", request);

        assertNotNull(response);
        assertNotNull(response.getInspectionDefectId());
        assertTrue(response.getInspectionDefectId().startsWith("INSPDEF-"));
        assertEquals("INSP-004", response.getInspectionId());
        assertEquals("PAINT_SCRATCH", response.getDefectCode());
        assertEquals("Scratch on the left door", response.getComment());
        assertNotNull(response.getCreatedDate());

        verify(inspectionDefectRepository).persist(any(InspectionDefect.class));
    }

    @Test
    public void addDefectToInspectionWithoutComment() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-005");
        inspection.setVehicleId("VEH-005");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final Defect defect = new Defect();
        defect.setCode("MISSING_SEAT");
        defect.setDescription("Seat is missing");
        defect.setPenaltyPoints(20);
        defect.setActive(true);
        defect.setCreatedDate(LocalDateTime.now());

        final AddDefectToInspectionRequest request = new AddDefectToInspectionRequest();
        request.setDefectCode("MISSING_SEAT");

        when(inspectionRepository.findById("INSP-005")).thenReturn(inspection);
        when(defectRepository.findById("MISSING_SEAT")).thenReturn(defect);
        when(inspectionDefectRepository.existsByInspectionIdAndDefectCode("INSP-005", "MISSING_SEAT")).thenReturn(
                false);

        final InspectionDefectResponse response = inspectionService.addDefectToInspection("INSP-005", request);

        assertNotNull(response);
        assertEquals("MISSING_SEAT", response.getDefectCode());
        assertNull(response.getComment());
    }

    @Test
    public void addDefectToInspectionNotFound() {
        final AddDefectToInspectionRequest request = new AddDefectToInspectionRequest();
        request.setDefectCode("PAINT_SCRATCH");

        when(inspectionRepository.findById("INSP-999")).thenReturn(null);

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> inspectionService.addDefectToInspection("INSP-999", request));

        assertEquals("INSPECTION_NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    public void addDefectToInspectionUnknownDefectCode() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-006");
        inspection.setVehicleId("VEH-006");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final AddDefectToInspectionRequest request = new AddDefectToInspectionRequest();
        request.setDefectCode("NOT_A_REAL_CODE");

        when(inspectionRepository.findById("INSP-006")).thenReturn(inspection);
        when(defectRepository.findById("NOT_A_REAL_CODE")).thenReturn(null);

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> inspectionService.addDefectToInspection("INSP-006", request));

        assertEquals("INVALID_DEFECT_CODE", exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());
        assertEquals("Invalid defect code 'NOT_A_REAL_CODE'", exception.getMessage());

    }

    @Test
    public void addDefectToInspectionAlreadyLinked() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-007");
        inspection.setVehicleId("VEH-007");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final Defect defect = new Defect();
        defect.setCode("DOOR_ALIGNMENT");
        defect.setDescription("Door alignment issue");
        defect.setPenaltyPoints(8);
        defect.setActive(true);
        defect.setCreatedDate(LocalDateTime.now());

        final AddDefectToInspectionRequest request = new AddDefectToInspectionRequest();
        request.setDefectCode("DOOR_ALIGNMENT");

        when(inspectionRepository.findById("INSP-007")).thenReturn(inspection);
        when(defectRepository.findById("DOOR_ALIGNMENT")).thenReturn(defect);
        when(inspectionDefectRepository.existsByInspectionIdAndDefectCode("INSP-007", "DOOR_ALIGNMENT")).thenReturn(
                true);

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> inspectionService.addDefectToInspection("INSP-007", request));

        assertEquals("DEFECT_ALREADY_LINKED", exception.getErrorCode());
        assertEquals(409, exception.getStatusCode());
        assertEquals("Defect 'DOOR_ALIGNMENT' is already linked to inspection 'INSP-007'", exception.getMessage());
    }

    @Test
    public void calculateScoreWithTwoDefects() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-010");
        inspection.setVehicleId("VEH-010");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect paintLink = new InspectionDefect();
        paintLink.setInspectionDefectId("INSPDEF-010");
        paintLink.setInspectionId("INSP-010");
        paintLink.setDefectCode("PAINT_SCRATCH");
        paintLink.setCreatedDate(LocalDateTime.now());

        final InspectionDefect doorLink = new InspectionDefect();
        doorLink.setInspectionDefectId("INSPDEF-011");
        doorLink.setInspectionId("INSP-010");
        doorLink.setDefectCode("DOOR_ALIGNMENT");
        doorLink.setCreatedDate(LocalDateTime.now());

        final Defect paintScratch = new Defect();
        paintScratch.setCode("PAINT_SCRATCH");
        paintScratch.setDescription("Surface paint scratch");
        paintScratch.setPenaltyPoints(10);

        final Defect doorAlignment = new Defect();
        doorAlignment.setCode("DOOR_ALIGNMENT");
        doorAlignment.setDescription("Door does not close flush");
        doorAlignment.setPenaltyPoints(15);

        when(inspectionRepository.findById("INSP-010")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-010")).thenReturn(List.of(paintLink, doorLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(paintScratch, doorAlignment));

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-010");

        assertNotNull(response);
        assertEquals("INSP-010", response.getInspectionId());
        assertEquals("VEH-010", response.getVehicleId());
        assertEquals(75, response.getScore());
        assertEquals(2, response.getDefectsCount());
        assertNotNull(response.getCalculatedAt());

        // The score must be persisted on the inspection, not only returned.
        assertEquals(75, inspection.getScore());
        assertNotNull(inspection.getCalculatedAt());
        verify(inspectionRepository).persist(inspection);
    }

    @Test
    public void calculateScoreWithoutDefects() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-011");
        inspection.setVehicleId("VEH-011");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        when(inspectionRepository.findById("INSP-011")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-011")).thenReturn(new ArrayList<>());

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-011");

        assertEquals(100, response.getScore());
        assertEquals(0, response.getDefectsCount());
        assertEquals(100, inspection.getScore());
        verify(inspectionRepository).persist(inspection);
    }

    @Test
    public void calculateScoreWithSingleDefect() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-012");
        inspection.setVehicleId("VEH-012");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect batteryLink = new InspectionDefect();
        batteryLink.setInspectionDefectId("INSPDEF-012");
        batteryLink.setInspectionId("INSP-012");
        batteryLink.setDefectCode("BATTERY_FAILURE");
        batteryLink.setCreatedDate(LocalDateTime.now());

        final Defect batteryFailure = new Defect();
        batteryFailure.setCode("BATTERY_FAILURE");
        batteryFailure.setDescription("Battery cell failure");
        batteryFailure.setPenaltyPoints(35);

        when(inspectionRepository.findById("INSP-012")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-012")).thenReturn(List.of(batteryLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(batteryFailure));

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-012");

        assertEquals(65, response.getScore());
        assertEquals(1, response.getDefectsCount());
        assertEquals(65, inspection.getScore());
    }

    @Test
    public void calculateScoreWithTheWholeCatalog() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-013");
        inspection.setVehicleId("VEH-013");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.REWORK_REQUIRED);

        final Map<String, Integer> penaltyPointsByCode =
                Map.of("PAINT_SCRATCH", 10, "BATTERY_FAILURE", 35, "DOOR_ALIGNMENT", 15, "MISSING_SEAT", 20);

        final List<InspectionDefect> links = new ArrayList<>();
        final List<Defect> defects = new ArrayList<>();

        for (final Map.Entry<String, Integer> entry : penaltyPointsByCode.entrySet()) {
            final InspectionDefect link = new InspectionDefect();
            link.setInspectionDefectId("INSPDEF-" + entry.getKey());
            link.setInspectionId("INSP-013");
            link.setDefectCode(entry.getKey());
            link.setCreatedDate(LocalDateTime.now());
            links.add(link);

            final Defect defect = new Defect();
            defect.setCode(entry.getKey());
            defect.setPenaltyPoints(entry.getValue());
            defects.add(defect);
        }

        when(inspectionRepository.findById("INSP-013")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-013")).thenReturn(links);
        when(defectRepository.findByCodes(anyCollection())).thenReturn(defects);

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-013");

        // 100 - (10 + 35 + 15 + 20) = 20
        assertEquals(20, response.getScore());
        assertEquals(4, response.getDefectsCount());
    }

    @Test
    public void calculateScoreIsClampedAtZeroWhenPenaltiesExceedOneHundred() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-014");
        inspection.setVehicleId("VEH-014");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.FAILED);

        final InspectionDefect firstLink = new InspectionDefect();
        firstLink.setInspectionDefectId("INSPDEF-014");
        firstLink.setInspectionId("INSP-014");
        firstLink.setDefectCode("BATTERY_FAILURE");
        firstLink.setCreatedDate(LocalDateTime.now());

        final InspectionDefect secondLink = new InspectionDefect();
        secondLink.setInspectionDefectId("INSPDEF-015");
        secondLink.setInspectionId("INSP-014");
        secondLink.setDefectCode("FRAME_CRACK");
        secondLink.setCreatedDate(LocalDateTime.now());

        final Defect batteryFailure = new Defect();
        batteryFailure.setCode("BATTERY_FAILURE");
        batteryFailure.setPenaltyPoints(35);

        final Defect frameCrack = new Defect();
        frameCrack.setCode("FRAME_CRACK");
        frameCrack.setPenaltyPoints(90);

        when(inspectionRepository.findById("INSP-014")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-014")).thenReturn(List.of(firstLink, secondLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(batteryFailure, frameCrack));

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-014");

        // 100 - 125 = -25, clamped to 0.
        assertEquals(0, response.getScore());
        assertEquals(2, response.getDefectsCount());
        assertEquals(0, inspection.getScore());
    }

    @Test
    public void calculateScoreIgnoresDefectsMissingFromTheCatalog() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-015");
        inspection.setVehicleId("VEH-015");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect knownLink = new InspectionDefect();
        knownLink.setInspectionDefectId("INSPDEF-016");
        knownLink.setInspectionId("INSP-015");
        knownLink.setDefectCode("PAINT_SCRATCH");
        knownLink.setCreatedDate(LocalDateTime.now());

        final InspectionDefect orphanLink = new InspectionDefect();
        orphanLink.setInspectionDefectId("INSPDEF-017");
        orphanLink.setInspectionId("INSP-015");
        orphanLink.setDefectCode("DELETED_CODE");
        orphanLink.setCreatedDate(LocalDateTime.now());

        final Defect paintScratch = new Defect();
        paintScratch.setCode("PAINT_SCRATCH");
        paintScratch.setPenaltyPoints(10);

        when(inspectionRepository.findById("INSP-015")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-015")).thenReturn(List.of(knownLink, orphanLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(paintScratch));

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-015");

        // Only the known defect is penalized; the orphan link counts as 0 points.
        assertEquals(90, response.getScore());
        assertEquals(2, response.getDefectsCount());
    }

    @Test
    public void calculateScoreNotFound() {
        when(inspectionRepository.findById("INSP-999")).thenReturn(null);

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> inspectionService.calculateScore("INSP-999"));

        assertEquals("INSPECTION_NOT_FOUND", exception.getErrorCode());
        assertEquals("Inspection 'INSP-999' not found", exception.getMessage());
        verifyNoInteractions(inspectionDefectRepository);
    }

    @Test
    public void calculateScoreStoresAReworkDecision() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-016");
        inspection.setVehicleId("VEH-016");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect paintLink = new InspectionDefect();
        paintLink.setInspectionDefectId("INSPDEF-018");
        paintLink.setInspectionId("INSP-016");
        paintLink.setDefectCode("PAINT_SCRATCH");
        paintLink.setCreatedDate(LocalDateTime.now());

        final InspectionDefect doorLink = new InspectionDefect();
        doorLink.setInspectionDefectId("INSPDEF-019");
        doorLink.setInspectionId("INSP-016");
        doorLink.setDefectCode("DOOR_ALIGNMENT");
        doorLink.setCreatedDate(LocalDateTime.now());

        final Defect paintScratch = new Defect();
        paintScratch.setCode("PAINT_SCRATCH");
        paintScratch.setPenaltyPoints(10);
        paintScratch.setAffectedStage(ProductionStage.PAINT);

        final Defect doorAlignment = new Defect();
        doorAlignment.setCode("DOOR_ALIGNMENT");
        doorAlignment.setPenaltyPoints(15);
        doorAlignment.setAffectedStage(ProductionStage.INTERIOR);

        when(inspectionRepository.findById("INSP-016")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-016")).thenReturn(List.of(paintLink, doorLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(paintScratch, doorAlignment));

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-016");

        // 100 - (10 + 15) = 75; REWORK; PAINT before INTERIOR
        assertEquals(75, response.getScore());
        assertEquals(Decision.REWORK, response.getDecision());
        assertEquals(ProductionStage.PAINT, response.getTargetStage());

        assertEquals(Decision.REWORK, inspection.getDecision());
        assertEquals(ProductionStage.PAINT, inspection.getTargetStage());
        assertEquals(InspectionStatus.REWORK_REQUIRED, inspection.getStatus());
        assertNotNull(inspection.getDecidedAt());
    }

    @Test
    public void calculateScoreStoresAPassDecisionWithoutTargetStage() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-017");
        inspection.setVehicleId("VEH-017");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        when(inspectionRepository.findById("INSP-017")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-017")).thenReturn(new ArrayList<>());

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-017");

        assertEquals(100, response.getScore());
        assertEquals(Decision.PASS, response.getDecision());
        assertNull(response.getTargetStage());
        assertEquals(InspectionStatus.APPROVED, inspection.getStatus());
    }

    @Test
    public void calculateScoreStoresAFailDecisionWithoutTargetStage() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-018");
        inspection.setVehicleId("VEH-018");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect batteryLink = new InspectionDefect();
        batteryLink.setInspectionDefectId("INSPDEF-020");
        batteryLink.setInspectionId("INSP-018");
        batteryLink.setDefectCode("BATTERY_FAILURE");
        batteryLink.setCreatedDate(LocalDateTime.now());

        final InspectionDefect seatLink = new InspectionDefect();
        seatLink.setInspectionDefectId("INSPDEF-021");
        seatLink.setInspectionId("INSP-018");
        seatLink.setDefectCode("MISSING_SEAT");
        seatLink.setCreatedDate(LocalDateTime.now());

        final Defect batteryFailure = new Defect();
        batteryFailure.setCode("BATTERY_FAILURE");
        batteryFailure.setPenaltyPoints(35);

        final Defect missingSeat = new Defect();
        missingSeat.setCode("MISSING_SEAT");
        missingSeat.setPenaltyPoints(20);

        when(inspectionRepository.findById("INSP-018")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-018")).thenReturn(List.of(batteryLink, seatLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(batteryFailure, missingSeat));

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-018");

        // 100 - (35 + 20) = 45; FAIL; vehicle discarded
        assertEquals(45, response.getScore());
        assertEquals(Decision.FAIL, response.getDecision());
        assertNull(response.getTargetStage());
        assertEquals(InspectionStatus.FAILED, inspection.getStatus());
    }

    @Test
    public void calculateScoreLeavesTargetStageNullWhenNoDefectIsMapped() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-019");
        inspection.setVehicleId("VEH-019");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect unknownLink = new InspectionDefect();
        unknownLink.setInspectionDefectId("INSPDEF-022");
        unknownLink.setInspectionId("INSP-019");
        unknownLink.setDefectCode("UNKNOWN_CODE");
        unknownLink.setCreatedDate(LocalDateTime.now());

        final Defect windscreenChip = new Defect();
        windscreenChip.setCode("UNKNOWN_CODE");
        windscreenChip.setPenaltyPoints(30);

        when(inspectionRepository.findById("INSP-019")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-019")).thenReturn(List.of(unknownLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(windscreenChip));

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-019");

        // 100 - 30 = 70; REWORK; defect has no stage mapped. vehicle cannot be sent anywhere
        assertEquals(Decision.REWORK, response.getDecision());
        assertNull(response.getTargetStage());
    }

    @Test
    public void calculateScoreOverwritesAPreviousDecision() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-020");
        inspection.setVehicleId("VEH-020");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.REWORK_REQUIRED);
        inspection.setScore(75);
        inspection.setDecision(Decision.REWORK);
        inspection.setTargetStage(ProductionStage.PAINT);

        when(inspectionRepository.findById("INSP-020")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-020")).thenReturn(new ArrayList<>());

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-020");

        // Defects are gone. Both production stage and old REWORK must be replaced
        assertEquals(100, response.getScore());
        assertEquals(Decision.PASS, response.getDecision());
        assertNull(inspection.getTargetStage());
        assertEquals(InspectionStatus.APPROVED, inspection.getStatus());
    }

    @Test
    public void findDecisionById() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-021");
        inspection.setVehicleId("VEH-021");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.REWORK_REQUIRED);
        inspection.setScore(72);
        inspection.setDecision(Decision.REWORK);
        inspection.setTargetStage(ProductionStage.PAINT);

        when(inspectionRepository.findById("INSP-021")).thenReturn(inspection);

        final InspectionDecisionResponse response = inspectionService.findDecisionById("INSP-021");

        assertEquals("INSP-021", response.getInspectionId());
        assertEquals(Decision.REWORK, response.getDecision());
        assertEquals(ProductionStage.PAINT, response.getTargetStage());
    }

    @Test
    public void findDecisionByIdNotFound() {
        when(inspectionRepository.findById("INSP-999")).thenReturn(null);

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> inspectionService.findDecisionById("INSP-999"));

        assertEquals("INSPECTION_NOT_FOUND", exception.getErrorCode());
        assertEquals("Inspection 'INSP-999' not found", exception.getMessage());
    }

    @Test
    public void calculateScorePublishesExactlyOneApprovedEvent() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-030");
        inspection.setVehicleId("VEH-030");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        when(inspectionRepository.findById("INSP-030")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-030")).thenReturn(new ArrayList<>());

        inspectionService.calculateScore("INSP-030");

        final ArgumentCaptor<QualityApprovedPayload> captor = ArgumentCaptor.forClass(QualityApprovedPayload.class);
        verify(approvedEvent).fire(captor.capture());

        verifyNoMoreInteractions(reworkRequiredEvent, failedEvent);

        assertEquals("INSP-030", captor.getValue().inspectionId());
        assertEquals("VEH-030", captor.getValue().vehicleId());
        assertEquals(100, captor.getValue().score());
        assertNotNull(captor.getValue().decidedAt());
    }

    @Test
    public void calculateScorePublishesExactlyOneReworkRequiredEvent() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-031");
        inspection.setVehicleId("VEH-031");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect paintLink = new InspectionDefect();
        paintLink.setInspectionDefectId("INSPDEF-031");
        paintLink.setInspectionId("INSP-031");
        paintLink.setDefectCode("PAINT_SCRATCH");
        paintLink.setCreatedDate(LocalDateTime.now());

        final Defect paintScratch = new Defect();
        paintScratch.setCode("PAINT_SCRATCH");
        paintScratch.setDescription("PAINT_SCRATCH detected");
        paintScratch.setPenaltyPoints(28);
        paintScratch.setAffectedStage(ProductionStage.PAINT);

        when(inspectionRepository.findById("INSP-031")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-031")).thenReturn(List.of(paintLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(paintScratch));

        inspectionService.calculateScore("INSP-031");

        final ArgumentCaptor<QualityReworkRequiredPayload> captor =
                ArgumentCaptor.forClass(QualityReworkRequiredPayload.class);
        verify(reworkRequiredEvent).fire(captor.capture());
        verifyNoMoreInteractions(approvedEvent, failedEvent);

        // 100 - 28 = 72; REWORK + correct reason
        assertEquals(72, captor.getValue().score());
        assertEquals(ProductionStage.PAINT, captor.getValue().targetStage());
        assertEquals("PAINT_SCRATCH detected", captor.getValue().reason());
    }

    @Test
    public void calculateScorePublishesExactlyOneFailedEvent() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-032");
        inspection.setVehicleId("VEH-032");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect batteryLink = new InspectionDefect();
        batteryLink.setInspectionDefectId("INSPDEF-032");
        batteryLink.setInspectionId("INSP-032");
        batteryLink.setDefectCode("BATTERY_FAILURE");
        batteryLink.setCreatedDate(LocalDateTime.now());

        final InspectionDefect seatLink = new InspectionDefect();
        seatLink.setInspectionDefectId("INSPDEF-033");
        seatLink.setInspectionId("INSP-032");
        seatLink.setDefectCode("MISSING_SEAT");
        seatLink.setCreatedDate(LocalDateTime.now());

        final Defect batteryFailure = new Defect();
        batteryFailure.setCode("BATTERY_FAILURE");
        batteryFailure.setPenaltyPoints(35);

        final Defect missingSeat = new Defect();
        missingSeat.setCode("MISSING_SEAT");
        missingSeat.setPenaltyPoints(20);

        when(inspectionRepository.findById("INSP-032")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-032")).thenReturn(List.of(batteryLink, seatLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(batteryFailure, missingSeat));

        inspectionService.calculateScore("INSP-032");

        final ArgumentCaptor<QualityFailedPayload> captor = ArgumentCaptor.forClass(QualityFailedPayload.class);
        verify(failedEvent).fire(captor.capture());
        verifyNoMoreInteractions(approvedEvent, reworkRequiredEvent);

        assertEquals(45, captor.getValue().score());
        assertEquals(List.of("BATTERY_FAILURE", "MISSING_SEAT"), captor.getValue().defectCodes());
    }

    @Test
    public void calculateScorePublishesAReworkEventWithoutATargetStageWhenNoDefectIsMapped() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-033");
        inspection.setVehicleId("VEH-033");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect unknownLink = new InspectionDefect();
        unknownLink.setInspectionDefectId("INSPDEF-034");
        unknownLink.setInspectionId("INSP-033");
        unknownLink.setDefectCode("WINDSCREEN_CHIP");
        unknownLink.setCreatedDate(LocalDateTime.now());

        final Defect windscreenChip = new Defect();
        windscreenChip.setCode("WINDSCREEN_CHIP");
        windscreenChip.setPenaltyPoints(30);

        when(inspectionRepository.findById("INSP-033")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-033")).thenReturn(List.of(unknownLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(windscreenChip));

        inspectionService.calculateScore("INSP-033");

        final ArgumentCaptor<QualityReworkRequiredPayload> captor =
                ArgumentCaptor.forClass(QualityReworkRequiredPayload.class);
        verify(reworkRequiredEvent).fire(captor.capture());

        assertNull(captor.getValue().targetStage());
        assertNull(captor.getValue().reason());
    }

    @Test
    public void performInspectionSkipsAlreadyProcessedEvent() {
        when(processedEventRepository.findByIdOptional("EVT-1")).thenReturn(Optional.of(new ProcessedEvent()));

        inspectionService.performInspection("EVT-1", payload());

        verifyNoInteractions(inspectionRepository);
        verifyNoInteractions(inspectionDefectRepository);
    }

    @Test
    public void performInspectionMarksTheEventProcessed() {
        when(processedEventRepository.findByIdOptional("EVT-1")).thenReturn(Optional.empty());
        stubInspectionStore();

        inspectionService.performInspection("EVT-1", payload());

        final ArgumentCaptor<ProcessedEvent> captor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository).persist(captor.capture());

        assertEquals("EVT-1", captor.getValue().getEventId());
        assertEquals(Topics.VEHICLE_ASSEMBLED, captor.getValue().getEventType());
    }

    @Test
    public void performInspectionCreatesInspectionAndScoresIt() {
        when(processedEventRepository.findByIdOptional("EVT-1")).thenReturn(Optional.empty());
        stubInspectionStore();

        inspectionService.performInspection("EVT-1", payload());

        verify(inspectionRepository, atLeastOnce()).persist(any(Inspection.class));
    }

    @Test
    public void performInspectionUsesTheRobotInspector() {
        when(processedEventRepository.findByIdOptional("EVT-1")).thenReturn(Optional.empty());
        stubInspectionStore();

        final ArgumentCaptor<Inspection> captor = ArgumentCaptor.forClass(Inspection.class);

        inspectionService.performInspection("EVT-1", payload());

        verify(inspectionRepository, atLeastOnce()).persist(captor.capture());
        assertEquals("QA-ROBOT-01", captor.getValue().getInspector());
    }

    @Test
    public void performInspectionSeedsFromTheVehiclesReworkCount() {
        when(processedEventRepository.findByIdOptional("EVT-1")).thenReturn(Optional.empty());
        when(inspectionRepository.countReworks("VEH-001")).thenReturn(2L);
        stubInspectionStore();

        inspectionService.performInspection("EVT-1", payload());

        verify(defectSeeder).seedFor(eq(2L), anyCollection());
    }

    @Test
    public void reworkBecomesFailOnceTheReworkBudgetIsSpent() {
        final Inspection inspection = reworkScoringInspection();

        when(inspectionRepository.countReworks("VEH-020")).thenReturn(3L);

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-020");

        assertEquals(65, response.getScore());
        assertEquals(Decision.FAIL, response.getDecision());
        assertEquals(InspectionStatus.FAILED, inspection.getStatus());
        assertNull(inspection.getTargetStage());
        verify(failedEvent).fire(any(QualityFailedPayload.class));
        verifyNoInteractions(reworkRequiredEvent);
    }

    @Test
    public void reworkStaysReworkWhileTheBudgetLasts() {
        reworkScoringInspection();

        when(inspectionRepository.countReworks("VEH-020")).thenReturn(2L);

        final InspectionScoreResponse response = inspectionService.calculateScore("INSP-020");

        assertEquals(Decision.REWORK, response.getDecision());
        verify(reworkRequiredEvent).fire(any(QualityReworkRequiredPayload.class));
        verifyNoInteractions(failedEvent);
    }

    @Test
    public void addDefectToInspectionRecomputesTheScore() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-040");
        inspection.setVehicleId("VEH-040");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final Defect paintScratch = new Defect();
        paintScratch.setCode("PAINT_SCRATCH");
        paintScratch.setPenaltyPoints(10);
        paintScratch.setActive(true);
        paintScratch.setAffectedStage(ProductionStage.PAINT);

        final InspectionDefect paintLink = new InspectionDefect();
        paintLink.setInspectionDefectId("INSPDEF-040");
        paintLink.setInspectionId("INSP-040");
        paintLink.setDefectCode("PAINT_SCRATCH");
        paintLink.setCreatedDate(LocalDateTime.now());

        final AddDefectToInspectionRequest request = new AddDefectToInspectionRequest();
        request.setDefectCode("PAINT_SCRATCH");

        when(inspectionRepository.findById("INSP-040")).thenReturn(inspection);
        when(defectRepository.findById("PAINT_SCRATCH")).thenReturn(paintScratch);
        when(inspectionDefectRepository.existsByInspectionIdAndDefectCode("INSP-040", "PAINT_SCRATCH")).thenReturn(
                false);
        when(inspectionDefectRepository.findByInspectionId("INSP-040")).thenReturn(List.of(paintLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(paintScratch));

        inspectionService.addDefectToInspection("INSP-040", request);

        // 100 - 10 = 90; the score is stored on the inspection, not only computed
        assertEquals(90, inspection.getScore());
        assertEquals(Decision.PASS, inspection.getDecision());
        assertNotNull(inspection.getCalculatedAt());
        verify(inspectionRepository).persist(inspection);
    }

    @Test
    public void addDefectToInspectionTellsNobody() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-041");
        inspection.setVehicleId("VEH-041");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final Defect batteryFailure = new Defect();
        batteryFailure.setCode("BATTERY_FAILURE");
        batteryFailure.setPenaltyPoints(35);
        batteryFailure.setActive(true);
        batteryFailure.setAffectedStage(ProductionStage.POWERTRAIN);

        final InspectionDefect batteryLink = new InspectionDefect();
        batteryLink.setInspectionDefectId("INSPDEF-041");
        batteryLink.setInspectionId("INSP-041");
        batteryLink.setDefectCode("BATTERY_FAILURE");
        batteryLink.setCreatedDate(LocalDateTime.now());

        final AddDefectToInspectionRequest request = new AddDefectToInspectionRequest();
        request.setDefectCode("BATTERY_FAILURE");

        when(inspectionRepository.findById("INSP-041")).thenReturn(inspection);
        when(defectRepository.findById("BATTERY_FAILURE")).thenReturn(batteryFailure);
        when(inspectionDefectRepository.existsByInspectionIdAndDefectCode("INSP-041", "BATTERY_FAILURE")).thenReturn(
                false);
        when(inspectionDefectRepository.findByInspectionId("INSP-041")).thenReturn(List.of(batteryLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(batteryFailure));

        inspectionService.addDefectToInspection("INSP-041", request);

        // 100 - 35 = 65 is a REWORK; no event published yet
        assertEquals(Decision.REWORK, inspection.getDecision());
        verifyNoInteractions(approvedEvent, reworkRequiredEvent, failedEvent);
    }

    private Inspection reworkScoringInspection() {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-020");
        inspection.setVehicleId("VEH-020");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);

        final InspectionDefect batteryLink = new InspectionDefect();
        batteryLink.setInspectionDefectId("INSPDEF-020");
        batteryLink.setInspectionId("INSP-020");
        batteryLink.setDefectCode("BATTERY_FAILURE");
        batteryLink.setCreatedDate(LocalDateTime.now());

        final Defect batteryFailure = new Defect();
        batteryFailure.setCode("BATTERY_FAILURE");
        batteryFailure.setDescription("Battery cell failure");
        batteryFailure.setPenaltyPoints(35);
        batteryFailure.setAffectedStage(ProductionStage.POWERTRAIN);

        when(inspectionRepository.findById("INSP-020")).thenReturn(inspection);
        when(inspectionDefectRepository.findByInspectionId("INSP-020")).thenReturn(List.of(batteryLink));
        when(defectRepository.findByCodes(anyCollection())).thenReturn(List.of(batteryFailure));

        return inspection;
    }

    private VehicleAssembledPayload payload() {
        return new VehicleAssembledPayload("VEH-001", "ORD-001", "MODEL-X", "LINE-1", Instant.now());
    }

    private void stubInspectionStore() {
        final List<Inspection> persisted = new ArrayList<>();

        doAnswer(invocation -> {
            persisted.add(invocation.getArgument(0));
            return null;
        }).when(inspectionRepository).persist(any(Inspection.class));

        when(inspectionRepository.findById(anyString())).thenAnswer(invocation -> persisted.stream()
                .filter(inspection -> inspection.getInspectionId().equals(invocation.getArgument(0))).findFirst()
                .orElse(null));
    }

}