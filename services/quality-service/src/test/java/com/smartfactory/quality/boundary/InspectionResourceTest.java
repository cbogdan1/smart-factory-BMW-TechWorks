package com.smartfactory.quality.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import com.smartfactory.quality.boundary.resources.InspectionResource;
import com.smartfactory.quality.control.services.InspectionService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InspectionResourceTest {

    @Mock
    InspectionService inspectionService;

    @InjectMocks
    InspectionResource inspectionResource;

    @Test
    public void createInspection() {
        final CreateInspectionRequest request = new CreateInspectionRequest();
        request.setVehicleId("VEH-001");

        final InspectionResponse expectedResponse = new InspectionResponse();
        expectedResponse.setInspectionId("INSP-001");
        expectedResponse.setVehicleId("VEH-001");
        expectedResponse.setInspectionDate(LocalDateTime.now());
        expectedResponse.setStatus(InspectionStatus.PENDING);

        when(inspectionService.createInspection(request)).thenReturn(expectedResponse);

        final Response response = inspectionResource.createInspection(request);

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        final InspectionResponse body = (InspectionResponse) response.getEntity();

        assertNotNull(body);
        assertEquals("INSP-001", body.getInspectionId());
        assertEquals("VEH-001", body.getVehicleId());
        assertNotNull(body.getInspectionDate());
        assertEquals(InspectionStatus.PENDING, body.getStatus());
    }

    @Test
    public void findById() {
        final InspectionDefectView defectView = new InspectionDefectView();
        defectView.setDefectCode("PAINT_SCRATCH");
        defectView.setDescription("Paint scratch detected");
        defectView.setPenaltyPoints(5);
        defectView.setComment("Scratch on the left door");

        final InspectionView expectedView = new InspectionView();
        expectedView.setInspectionId("INSP-002");
        expectedView.setVehicleId("VEH-002");
        expectedView.setInspectionDate(LocalDateTime.now());
        expectedView.setStatus(InspectionStatus.PENDING);
        expectedView.setDefects(List.of(defectView));

        when(inspectionService.findById("INSP-002")).thenReturn(expectedView);

        final InspectionView response = inspectionResource.findById("INSP-002");

        assertNotNull(response);
        assertEquals("INSP-002", response.getInspectionId());
        assertEquals("VEH-002", response.getVehicleId());
        assertNotNull(response.getInspectionDate());
        assertEquals(InspectionStatus.PENDING, response.getStatus());
        assertEquals(1, response.getDefects().size());
        assertEquals("PAINT_SCRATCH", response.getDefects().get(0).getDefectCode());
        assertEquals("Paint scratch detected", response.getDefects().get(0).getDescription());
        assertEquals(5, response.getDefects().get(0).getPenaltyPoints());
        assertEquals("Scratch on the left door", response.getDefects().get(0).getComment());
    }

    @Test
    public void findByIdNotFound() {
        when(inspectionService.findById("INSP-999")).thenThrow(
                new BusinessException("Inspection 'INSP-999' not found", "INSPECTION_NOT_FOUND", 404));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> inspectionResource.findById("INSP-999"));

        assertEquals("INSPECTION_NOT_FOUND", exception.getErrorCode());
        assertEquals("Inspection 'INSP-999' not found", exception.getMessage());
    }

    @Test
    public void findAll() {
        final InspectionResponse inspectionResponse = new InspectionResponse();
        inspectionResponse.setInspectionId("INSP-003");
        inspectionResponse.setVehicleId("VEH-003");
        inspectionResponse.setInspectionDate(LocalDateTime.now());
        inspectionResponse.setStatus(InspectionStatus.PENDING);

        final List<InspectionResponse> inspections = new ArrayList<>();
        inspections.add(inspectionResponse);

        when(inspectionService.findAll()).thenReturn(inspections);

        final List<InspectionResponse> response = inspectionResource.findAll();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals("INSP-003", response.get(0).getInspectionId());
        assertEquals("VEH-003", response.get(0).getVehicleId());
        assertEquals(InspectionStatus.PENDING, response.get(0).getStatus());
    }

    @Test
    public void addDefectToInspection() {
        final AddDefectToInspectionRequest request = new AddDefectToInspectionRequest();
        request.setDefectCode("PAINT_SCRATCH");
        request.setComment("Scratch on the left door");

        final InspectionDefectResponse expectedResponse = new InspectionDefectResponse();
        expectedResponse.setInspectionDefectId("INSPDEF-001");
        expectedResponse.setInspectionId("INSP-004");
        expectedResponse.setDefectCode("PAINT_SCRATCH");
        expectedResponse.setComment("Scratch on the left door");
        expectedResponse.setCreatedDate(LocalDateTime.now());

        when(inspectionService.addDefectToInspection("INSP-004", request)).thenReturn(expectedResponse);

        final Response response = inspectionResource.addDefectToInspection("INSP-004", request);

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        final InspectionDefectResponse body = (InspectionDefectResponse) response.getEntity();

        assertNotNull(body);
        assertEquals("INSPDEF-001", body.getInspectionDefectId());
        assertEquals("INSP-004", body.getInspectionId());
        assertEquals("PAINT_SCRATCH", body.getDefectCode());
        assertEquals("Scratch on the left door", body.getComment());
        assertNotNull(body.getCreatedDate());
    }

    @Test
    public void calculateScore() {
        final InspectionScoreResponse expectedResponse = new InspectionScoreResponse();
        expectedResponse.setInspectionId("INSP-005");
        expectedResponse.setVehicleId("VEH-005");
        expectedResponse.setScore(75);
        expectedResponse.setDefectsCount(2);
        expectedResponse.setCalculatedAt(LocalDateTime.now());

        when(inspectionService.calculateScore("INSP-005")).thenReturn(expectedResponse);

        final InspectionScoreResponse response = inspectionResource.calculateScore("INSP-005");

        assertNotNull(response);
        assertEquals("INSP-005", response.getInspectionId());
        assertEquals("VEH-005", response.getVehicleId());
        assertEquals(75, response.getScore());
        assertEquals(2, response.getDefectsCount());
        assertNotNull(response.getCalculatedAt());
    }

    @Test
    public void calculateScoreNotFound() {
        when(inspectionService.calculateScore("INSP-999")).thenThrow(
                new BusinessException("Inspection 'INSP-999' not found", "INSPECTION_NOT_FOUND", 404));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> inspectionResource.calculateScore("INSP-999"));

        assertEquals("INSPECTION_NOT_FOUND", exception.getErrorCode());
        assertEquals("Inspection 'INSP-999' not found", exception.getMessage());
    }

    @Test
    public void findDecisionById() {
        final InspectionDecisionResponse expectedResponse = new InspectionDecisionResponse();
        expectedResponse.setInspectionId("INSP-002");
        expectedResponse.setDecision(Decision.REWORK);
        expectedResponse.setTargetStage(ProductionStage.PAINT);

        when(inspectionService.findDecisionById("INSP-002")).thenReturn(expectedResponse);

        final InspectionDecisionResponse response = inspectionResource.findDecisionById("INSP-002");

        assertNotNull(response);
        assertEquals("INSP-002", response.getInspectionId());
        assertEquals(Decision.REWORK, response.getDecision());
        assertEquals(ProductionStage.PAINT, response.getTargetStage());
    }

    @Test
    public void findDecisionByIdNotFound() {
        when(inspectionService.findDecisionById("INSP-999")).thenThrow(
                new BusinessException("Inspection 'INSP-999' not found", "INSPECTION_NOT_FOUND", 404));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> inspectionResource.findDecisionById("INSP-999"));

        assertEquals("INSPECTION_NOT_FOUND", exception.getErrorCode());
        assertEquals("Inspection 'INSP-999' not found", exception.getMessage());
    }

}