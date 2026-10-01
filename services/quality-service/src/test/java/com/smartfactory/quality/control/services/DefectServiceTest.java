package com.smartfactory.quality.control.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.smartfactory.common.dto.quality.CreateDefectRequest;
import com.smartfactory.common.dto.quality.DefectResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.quality.control.repositories.DefectRepository;
import com.smartfactory.quality.entity.Defect;
import com.smartfactory.quality.mapper.DefectTestMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefectServiceTest {

    @Mock
    DefectRepository defectRepository;

    @InjectMocks
    DefectService defectService;

    @Test
    public void createDefect() {

        final CreateDefectRequest request = DefectTestMapper.toCreateRequest("PAINT_SCRATCH", "Paint scratch", 5);

        final DefectResponse response = defectService.createDefect(request);

        assertNotNull(response);
        assertNotNull(response.getDefectId());
        assertTrue(response.getDefectId().startsWith("DEF-"));
        assertEquals("PAINT_SCRATCH", response.getCode());
        assertEquals("Paint scratch", response.getDescription());
        assertEquals(5, response.getPenaltyPoints());
        assertTrue(response.getActive());
        assertNotNull(response.getCreatedDate());

        verify(defectRepository).persist(any(Defect.class));
    }

    @Test
    public void createDefectDuplicateCode() {

        final CreateDefectRequest request = DefectTestMapper.toCreateRequest("PAINT_SCRATCH");

        final Defect defect = DefectTestMapper.toDefect("PAINT_SCRATCH");

        when(defectRepository.findById("PAINT_SCRATCH")).thenReturn(defect);

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> defectService.createDefect(request));

        assertEquals("DEFECT_ALREADY_EXISTS", exception.getErrorCode());
        assertEquals(409, exception.getStatusCode());
        assertEquals("Defect with code 'PAINT_SCRATCH' already exists", exception.getMessage());
    }

    @Test
    public void findByCode() {

        final Defect defect = DefectTestMapper.toDefect("BATTERY_FAILURE", "Battery failure", 15);

        when(defectRepository.findById("BATTERY_FAILURE")).thenReturn(defect);

        final DefectResponse response = defectService.findByCode("BATTERY_FAILURE");

        assertNotNull(response);
        assertEquals("BATTERY_FAILURE", response.getCode());
        assertEquals("Battery failure", response.getDescription());
        assertEquals(15, response.getPenaltyPoints());
        assertTrue(response.getActive());
    }

    @Test
    public void findByCodeNotFound() {

        when(defectRepository.findById("UNKNOWN")).thenReturn(null);

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> defectService.findByCode("UNKNOWN"));

        assertEquals("DEFECT_NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
        assertEquals("Defect with code 'UNKNOWN' not found", exception.getMessage());
    }

    @Test
    public void findAll() {

        final Defect defect = DefectTestMapper.toDefect("MISSING_SEAT", "Missing seat", 20);

        final List<Defect> defects = new ArrayList<>();
        defects.add(defect);

        when(defectRepository.listAll()).thenReturn(defects);

        final List<DefectResponse> responses = defectService.findAll();

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("MISSING_SEAT", responses.get(0).getCode());
    }
}