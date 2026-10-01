package com.smartfactory.inventory.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import com.smartfactory.common.dto.inventory.MaterialDemandResponse;
import com.smartfactory.inventory.control.MaterialDemandService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaterialDemandResourceTest {

    @Mock
    MaterialDemandService materialDemandService;

    @InjectMocks
    MaterialDemandResource resource;

    @Test
    void shouldGetUpcomingDemands() {
        final List<MaterialDemandResponse> mockList = List.of(new MaterialDemandResponse());
        when(materialDemandService.getUpcomingDemands()).thenReturn(mockList);

        final Response response = resource.getUpcomingDemands();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockList, response.getEntity());
    }

    @Test
    void shouldGetAllMaterialDemands() {
        final List<MaterialDemandResponse> mockList = List.of(new MaterialDemandResponse());
        when(materialDemandService.getAllMaterialDemands()).thenReturn(mockList);

        final Response response = resource.getAllMaterialDemands();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockList, response.getEntity());
    }

    @Test
    void shouldGetDemandsByVehicleId() {
        final List<MaterialDemandResponse> mockList = List.of(new MaterialDemandResponse());
        when(materialDemandService.getDemandsByVehicleId("VEH-1")).thenReturn(mockList);

        final Response response = resource.getDemandsByVehicleId("VEH-1");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockList, response.getEntity());
    }

    @Test
    void shouldGetDemandsByPlanId() {
        final List<MaterialDemandResponse> mockList = List.of(new MaterialDemandResponse());
        when(materialDemandService.getByPlanId("PLAN-1")).thenReturn(mockList);

        final Response response = resource.getDemandsByPlanId("PLAN-1");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockList, response.getEntity());
    }
}