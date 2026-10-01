package com.smartfactory.quality.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.smartfactory.common.dto.quality.CreateDefectRequest;
import com.smartfactory.common.dto.quality.DefectResponse;
import com.smartfactory.quality.boundary.resources.DefectResource;
import com.smartfactory.quality.control.services.DefectService;
import com.smartfactory.quality.mapper.DefectTestMapper;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefectResourceTest {

    @Mock
    DefectService defectService;

    @InjectMocks
    DefectResource defectResource;

    @Test
    public void createDefect() {

        final CreateDefectRequest request = DefectTestMapper.toCreateRequest("PAINT_SCRATCH", "Paint scratch", 5);

        final DefectResponse expectedResponse = DefectTestMapper.toResponse("PAINT_SCRATCH", "Paint scratch", 5);

        when(defectService.createDefect(request)).thenReturn(expectedResponse);

        final Response response = defectResource.createDefect(request);

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        final DefectResponse body = (DefectResponse) response.getEntity();

        assertNotNull(body);
        assertEquals("PAINT_SCRATCH", body.getCode());
        assertEquals("Paint scratch", body.getDescription());
        assertEquals(5, body.getPenaltyPoints());
        assertEquals(true, body.getActive());
        assertNotNull(body.getCreatedDate());
    }

    @Test
    public void findByCode() {

        final DefectResponse expectedResponse = DefectTestMapper.toResponse("BATTERY_FAILURE", "Battery failure", 15);

        when(defectService.findByCode("BATTERY_FAILURE")).thenReturn(expectedResponse);

        final Response response = defectResource.findByCode("BATTERY_FAILURE");

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        final DefectResponse body = (DefectResponse) response.getEntity();

        assertNotNull(body);
        assertEquals("BATTERY_FAILURE", body.getCode());
        assertEquals("Battery failure", body.getDescription());
        assertEquals(15, body.getPenaltyPoints());
        assertEquals(true, body.getActive());
        assertNotNull(body.getCreatedDate());
    }

    @Test
    public void findAll() {

        final DefectResponse defect = DefectTestMapper.toResponse("DOOR_ALIGNMENT", "Door alignment", 8);

        final List<DefectResponse> defects = new ArrayList<>();
        defects.add(defect);

        when(defectService.findAll()).thenReturn(defects);

        final Response response = defectResource.findAll();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked") final List<DefectResponse> body = (List<DefectResponse>) response.getEntity();

        assertNotNull(body);
        assertFalse(body.isEmpty());
        assertEquals(1, body.size());

        assertEquals("DOOR_ALIGNMENT", body.get(0).getCode());
        assertEquals("Door alignment", body.get(0).getDescription());
        assertEquals(8, body.get(0).getPenaltyPoints());
        assertEquals(true, body.get(0).getActive());
        assertNotNull(body.get(0).getCreatedDate());
    }
}