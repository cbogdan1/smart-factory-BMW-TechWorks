package com.smartfactory.quality.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import com.smartfactory.common.dto.quality.InspectionView;
import com.smartfactory.common.enums.Decision;
import com.smartfactory.quality.boundary.resources.InspectionResource;
import com.smartfactory.quality.control.services.InspectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InspectionResourceHistoryTest {

    @Mock
    InspectionService inspectionService;

    @InjectMocks
    InspectionResource inspectionResource;

    @Test
    public void history_endpoint_forwardsFiltersAndReturnsList() {
        final InspectionView view = new InspectionView();
        view.setInspectionId("INSP-100");
        view.setVehicleId("VEH-100");
        view.setInspectionDate(LocalDateTime.now());
        view.setDecision(Decision.PASS);

        when(inspectionService.history("VEH-100", Decision.PASS)).thenReturn(List.of(view));

        final List<InspectionView> response = inspectionResource.history("VEH-100", Decision.PASS);

        assertEquals(1, response.size());
        assertEquals("INSP-100", response.get(0).getInspectionId());
        assertEquals("VEH-100", response.get(0).getVehicleId());
        assertEquals(Decision.PASS, response.get(0).getDecision());
    }

    @Test
    void history_withoutFilters_forwardsNulls() {
        final InspectionView view = new InspectionView();
        view.setInspectionId("INSP-100");
        view.setVehicleId("VEH-100");
        view.setInspectionDate(LocalDateTime.now());
        view.setDecision(Decision.PASS);

        when(inspectionService.history(null, null)).thenReturn(List.of(view));

        final List<InspectionView> response = inspectionResource.history(null, null);

        assertEquals(1, response.size());
        assertEquals("INSP-100", response.get(0).getInspectionId());
    }
}