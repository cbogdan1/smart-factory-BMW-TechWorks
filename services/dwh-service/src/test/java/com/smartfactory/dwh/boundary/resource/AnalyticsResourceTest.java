package com.smartfactory.dwh.boundary.resource;

import static com.smartfactory.common.EndpointPaths.ANALYTICS_SUMMARY;
import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.smartfactory.common.dto.dwh.AnalyticsSummaryResponse;
import com.smartfactory.dwh.control.service.AnalyticsService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AnalyticsResourceTest {

    @InjectMock
    AnalyticsService analyticsService;

    @Test
    void shouldReturn200() {

        when(analyticsService.getAnalyticsSummary()).thenReturn(
                new AnalyticsSummaryResponse(5L, 5L, 12L, 3L, 34L, Optional.of(Instant.now())));

        given().when().get(ANALYTICS_SUMMARY).then().statusCode(200).contentType("application/json");
    }

    @Test
    void shouldReturn500WhenServiceFails() {

        when(analyticsService.getAnalyticsSummary()).thenThrow(new RuntimeException("DB error"));

        given().when().get(ANALYTICS_SUMMARY).then().statusCode(500);
    }
}