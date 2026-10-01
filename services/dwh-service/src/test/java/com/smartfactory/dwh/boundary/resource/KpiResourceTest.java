package com.smartfactory.dwh.boundary.resource;

import static com.smartfactory.common.EndpointPaths.DASHBOARD_KPIS;
import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.smartfactory.common.dto.dwh.DashboardKpiResponse;
import com.smartfactory.dwh.control.service.KpiService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class KpiResourceTest {
    @InjectMock
    KpiService kpiService;

    @Test
    void shouldReturn200() {
        when(kpiService.getDashboardKpis()).thenReturn(
                new DashboardKpiResponse(5L, 5L, 5L, 5L, 7.3, 2L, 3L, Optional.of(Instant.now())));

        given().when().get(DASHBOARD_KPIS).then().statusCode(200).contentType("application/json");
    }

    @Test
    void shouldReturn500WhenServiceFails() {
        when(kpiService.getDashboardKpis()).thenThrow(new RuntimeException("DB error"));

        given().when().get(DASHBOARD_KPIS).then().statusCode(500);
    }
}
