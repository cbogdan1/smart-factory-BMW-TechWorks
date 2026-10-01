package com.smartfactory.quality.control.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import com.smartfactory.common.dto.quality.InspectionDefectView;
import com.smartfactory.common.dto.quality.InspectionView;
import com.smartfactory.common.enums.Decision;
import com.smartfactory.quality.control.repositories.DefectRepository;
import com.smartfactory.quality.control.repositories.InspectionDefectRepository;
import com.smartfactory.quality.control.repositories.InspectionRepository;
import com.smartfactory.quality.entity.Defect;
import com.smartfactory.quality.entity.Inspection;
import com.smartfactory.quality.entity.InspectionDefect;
import com.smartfactory.quality.entity.engine.InspectionDecisionEngine;
import jakarta.enterprise.event.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InspectionServiceHistoryTest {

    @Mock
    InspectionRepository inspectionRepository;

    @Mock
    InspectionDefectRepository inspectionDefectRepository;

    @Mock
    DefectRepository defectRepository;

    @Mock
    InspectionDecisionEngine inspectionDecisionEngine;

    @Mock
    Event approvedEvent;

    @Mock
    Event reworkRequiredEvent;

    @Mock
    Event failedEvent;

    @InjectMocks
    InspectionService inspectionService;

    @Test
    public void history_noFilters_returnsInspectionsWithDefectsEmbedded() {
        // prepare inspections
        final Inspection insp1 = new Inspection();
        insp1.setInspectionId("INSP-001");
        insp1.setVehicleId("VEH-001");
        insp1.setInspector("John Smith");
        insp1.setInspectionDate(LocalDateTime.now());

        final Inspection insp2 = new Inspection();
        insp2.setInspectionId("INSP-002");
        insp2.setVehicleId("VEH-002");
        insp2.setInspector("Jane Doe");
        insp2.setInspectionDate(LocalDateTime.now());
        insp2.setDecision(Decision.PASS);

        when(inspectionRepository.list("order by inspectionDate")).thenReturn(List.of(insp1, insp2));

        // first inspection has no defects
        when(inspectionDefectRepository.findByInspectionId("INSP-001")).thenReturn(List.of());

        // second inspection has one defect
        final InspectionDefect inspectionDefect = new InspectionDefect();
        inspectionDefect.setInspectionDefectId("INSPDEF-001");
        inspectionDefect.setInspectionId("INSP-002");
        inspectionDefect.setDefectCode("PAINT_SCRATCH");
        inspectionDefect.setComment("Hood scratch");

        when(inspectionDefectRepository.findByInspectionId("INSP-002")).thenReturn(List.of(inspectionDefect));

        final Defect defect = new Defect();
        defect.setCode("PAINT_SCRATCH");
        defect.setDescription("Paint scratch detected");
        defect.setPenaltyPoints(10);

        // defectRepository should be called with the codes from inspection defects
        when(defectRepository.findByCodes(List.of("PAINT_SCRATCH"))).thenReturn(List.of(defect));

        final List<InspectionView> result = inspectionService.history(null, null);

        assertEquals(2, result.size());

        final InspectionView pass = result.get(0);
        assertEquals("INSP-001", pass.getInspectionId());
        assertEquals("John Smith", pass.getInspector());
        assertEquals(0, pass.getDefects().size());

        final InspectionView rework = result.get(1);
        assertEquals("INSP-002", rework.getInspectionId());
        assertEquals(Decision.PASS, rework.getDecision());
        assertEquals("Jane Doe", rework.getInspector());
        assertEquals(1, rework.getDefects().size());

        final InspectionDefectView defectView = rework.getDefects().get(0);
        assertEquals("PAINT_SCRATCH", defectView.getDefectCode());
        assertEquals(10, defectView.getPenaltyPoints());
        assertEquals("Hood scratch", defectView.getComment());
    }

    @Test
    public void history_withDecisionFilter_returnsOnlyMatchingInspections() {
        final Inspection insp = new Inspection();
        insp.setInspectionId("INSP-003");
        insp.setDecision(Decision.REWORK);
        insp.setVehicleId("VEH-010");
        insp.setInspector("Jane Doe");

        when(inspectionRepository.list("decision = ?1 order by inspectionDate", Decision.REWORK)).thenReturn(
                List.of(insp));
        when(inspectionDefectRepository.findByInspectionId("INSP-003")).thenReturn(List.of());

        final List<InspectionView> result = inspectionService.history(null, Decision.REWORK);

        assertEquals(1, result.size());
        assertEquals("INSP-003", result.get(0).getInspectionId());
        assertEquals(Decision.REWORK, result.get(0).getDecision());
    }

    @Test
    public void history_withBothFilters_combinesThem() {
        final Inspection insp = new Inspection();
        insp.setInspectionId("INSP-004");
        insp.setVehicleId("VEH-002");
        insp.setDecision(Decision.REWORK);
        insp.setInspector("Jane Doe");

        when(inspectionRepository.list("vehicleId = ?1 and decision = ?2 order by inspectionDate", "VEH-002",
                Decision.REWORK)).thenReturn(List.of(insp));
        when(inspectionDefectRepository.findByInspectionId("INSP-004")).thenReturn(List.of());

        final List<InspectionView> result = inspectionService.history("VEH-002", Decision.REWORK);

        assertEquals(1, result.size());
        assertEquals("INSP-004", result.get(0).getInspectionId());
    }

}