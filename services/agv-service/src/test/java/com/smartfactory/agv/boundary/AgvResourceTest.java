package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.AGVS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import com.smartfactory.agv.control.service.AgvService;
import com.smartfactory.common.dto.agv.AgvResponse;
import com.smartfactory.common.enums.AgvStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AgvResourceTest {

    @InjectMock
    AgvService agvService;

    @Test
    void shouldGetAllAgvs() {

        final AgvResponse response =
                AgvResponse.builder().agvId("AGV-001").status(String.valueOf(AgvStatus.AVAILABLE)).nodeId("NODE-001")
                        .batteryLevel(100).createdDate(LocalDateTime.now()).lastUpdated(LocalDateTime.now()).build();

        when(agvService.getAllAgvs()).thenReturn(List.of(response));

        given().when().get(AGVS).then().statusCode(200).body("size()", is(1)).body("[0].agvId", is("AGV-001"));
    }

    @Test
    void shouldGetAgvById() {

        final AgvResponse response =
                AgvResponse.builder().agvId("AGV-001").status(String.valueOf(AgvStatus.AVAILABLE)).nodeId("NODE-001")
                        .batteryLevel(100).createdDate(LocalDateTime.now()).lastUpdated(LocalDateTime.now()).build();

        when(agvService.getAgv("AGV-001")).thenReturn(response);

        given().when().get(AGVS + "/AGV-001").then().statusCode(200).body("agvId", is("AGV-001"));
    }

    @Test
    void shouldCreateAgv() {

        final AgvResponse response =
                AgvResponse.builder().agvId("AGV-001").status(String.valueOf(AgvStatus.AVAILABLE)).nodeId("NODE-001")
                        .batteryLevel(100).createdDate(LocalDateTime.now()).lastUpdated(LocalDateTime.now()).build();

        when(agvService.createAgv(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        final String payload = """
                {
                  "status":"AVAILABLE",
                  "nodeId":"NODE-001",
                  "batteryLevel":100
                }
                """;

        given().contentType("application/json").body(payload).when().post(AGVS).then().statusCode(201)
                .body("agvId", is("AGV-001"));
    }
}