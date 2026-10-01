package com.smartfactory.planning.boundary;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.planning.CreateProductionPlanRequest;
import com.smartfactory.common.dto.planning.ProductionPlanResponse;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.enums.ProductionPlanStatus;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.PlanningServiceExceptions;
import com.smartfactory.planning.control.service.ProductionPlanService;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductionPlanResourceTest {
    @InjectMock
    ProductionPlanService productionPlanService;

    @Test
    void shouldCreatePlan() {
        when(productionPlanService.create(any(CreateProductionPlanRequest.class))).thenReturn(response());

        final CreateProductionPlanRequest request = new CreateProductionPlanRequest();

        request.setOrderId("ORD-000001");
        request.setVehicleId("VEH-000001");
        request.setVehicleModel("BMW i4");
        request.setPriority(Priority.HIGH);
        request.setPlannedStartDate(LocalDate.of(2026, 8, 10));

        given().contentType("application/json").body(request).when().post(EndpointPaths.PLANS).then().statusCode(200)
                .body("planId", equalTo("PLAN-000001")).body("orderId", equalTo("ORD-000001"))
                .body("vehicleId", equalTo("VEH-000001")).body("vehicleModel", equalTo("BMW i4"))
                .body("productionLine", equalTo("LINE-1")).body("priority", equalTo("HIGH"))
                .body("plannedStartDate", equalTo("2026-08-10")).body("status", equalTo("PLANNED"))
                .body("createdDate", notNullValue());
    }

    @Test
    void shouldGetPlanById() {
        when(productionPlanService.getById("PLAN-000001")).thenReturn(response());

        given().when().get(EndpointPaths.PLANS + "/PLAN-000001").then().statusCode(200)
                .body("planId", equalTo("PLAN-000001")).body("orderId", equalTo("ORD-000001"))
                .body("vehicleId", equalTo("VEH-000001")).body("vehicleModel", equalTo("BMW i4"))
                .body("productionLine", equalTo("LINE-1")).body("priority", equalTo("HIGH"))
                .body("plannedStartDate", equalTo("2026-08-10")).body("status", equalTo("PLANNED"))
                .body("createdDate", notNullValue());
    }

    @Test
    void shouldGetAllPlans() {
        when(productionPlanService.getAll()).thenReturn(List.of(response(), response()));

        given().when().get(EndpointPaths.PLANS).then().statusCode(200).body("size()", equalTo(2));
    }

    @Test
    void shouldReturnBusinessErrorWhenPlanNotFound() {
        when(productionPlanService.getById("PLAN-999")).thenThrow(
                new BusinessException("Production plan not found: PLAN-999", PlanningServiceExceptions.PLAN_NOT_FOUND,
                        404));

        given().when().get(EndpointPaths.PLANS + "/PLAN-999").then().statusCode(404)
                .body("message", equalTo("Production plan not found: PLAN-999"))
                .body("code", equalTo(PlanningServiceExceptions.PLAN_NOT_FOUND)).body("timestamp", notNullValue());
    }

    @Test
    void shouldReturnConflictWhenProductionLineCapacityExceeded() {
        when(productionPlanService.create(any(CreateProductionPlanRequest.class))).thenThrow(
                new BusinessException("Production line capacity exceeded for LINE-1 (5/5)",
                        PlanningServiceExceptions.PRODUCTION_LINE_CAPACITY_EXCEEDED, 409));

        final CreateProductionPlanRequest request = createRequest();

        given().contentType("application/json").body(request).when().post(EndpointPaths.PLANS).then().statusCode(409)
                .body("message", equalTo("Production line capacity exceeded for LINE-1 (5/5)"))
                .body("code", equalTo(PlanningServiceExceptions.PRODUCTION_LINE_CAPACITY_EXCEEDED))
                .body("timestamp", notNullValue());
    }

    @Test
    void shouldReturnServiceUnavailableWhenNoProductionLineAvailable() {
        when(productionPlanService.create(any(CreateProductionPlanRequest.class))).thenThrow(
                new BusinessException("No enabled production line found",
                        PlanningServiceExceptions.NO_PRODUCTION_LINE_AVAILABLE, 503));

        final CreateProductionPlanRequest request = createRequest();

        given().contentType("application/json").body(request).when().post(EndpointPaths.PLANS).then().statusCode(503)
                .body("message", equalTo("No enabled production line found"))
                .body("code", equalTo(PlanningServiceExceptions.NO_PRODUCTION_LINE_AVAILABLE))
                .body("timestamp", notNullValue());
    }

    private CreateProductionPlanRequest createRequest() {
        final CreateProductionPlanRequest request = new CreateProductionPlanRequest();

        request.setOrderId("ORD-000001");
        request.setVehicleId("VEH-000001");
        request.setVehicleModel("BMW i4");
        request.setPriority(Priority.HIGH);
        request.setPlannedStartDate(LocalDate.of(2026, 8, 10));

        return request;
    }

    private ProductionPlanResponse response() {
        final ProductionPlanResponse response = new ProductionPlanResponse();

        response.setPlanId("PLAN-000001");
        response.setOrderId("ORD-000001");
        response.setVehicleId("VEH-000001");
        response.setVehicleModel("BMW i4");
        response.setProductionLine("LINE-1");
        response.setPriority(Priority.HIGH);
        response.setPlannedStartDate(LocalDate.of(2026, 8, 10));
        response.setStatus(ProductionPlanStatus.PLANNED);
        response.setCreatedDate(LocalDateTime.of(2026, 8, 10, 10, 0));

        return response;
    }
}