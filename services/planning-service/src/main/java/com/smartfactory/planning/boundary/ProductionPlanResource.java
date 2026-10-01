package com.smartfactory.planning.boundary;

import java.util.List;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.planning.CreateProductionPlanRequest;
import com.smartfactory.common.dto.planning.ProductionPlanResponse;
import com.smartfactory.planning.control.service.ProductionPlanService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.PLANS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Production Plans", description = "Operations for creating and retrieving production plans")
public class ProductionPlanResource {
    @Inject
    ProductionPlanService productionPlanService;

    @POST
    @Operation(operationId = "createProductionPlan", summary = "Create a production plan", description = """
            Creates a new production plan for an order and vehicle.
            The created plan can be broadcast as a production-planned
            event for downstream services such as Inventory and DWH.
            """)
    @RequestBody(required = true, description = "Information required to create a production plan")
    @APIResponses({ @APIResponse(responseCode = "200", description = "Production plan created successfully"),
            @APIResponse(responseCode = "400", description = "The production plan request is invalid"),
            @APIResponse(responseCode = "409", description = "Production line capacity exceeded"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred while creating the plan") })
    public ProductionPlanResponse createPlan(@Valid final CreateProductionPlanRequest request) {
        return productionPlanService.create(request);
    }

    @GET
    @Path("/{planId}")
    @Operation(operationId = "getProductionPlanById", summary = "Get a production plan by ID", description = """
            Returns the production plan identified by the supplied
            production plan ID.
            """)
    @APIResponses({ @APIResponse(responseCode = "200", description = "Production plan found"),
            @APIResponse(responseCode = "404", description = "Production plan not found"),
            @APIResponse(responseCode = "500",
                    description = "An unexpected error occurred while retrieving the plan") })
    public ProductionPlanResponse getPlan(@PathParam("planId") final String planId) {
        return productionPlanService.getById(planId);
    }

    @GET
    @Operation(operationId = "getProductionPlans", summary = "Get all production plans", description = """
            Returns all production plans currently available in the
            planning service.
            """)
    @APIResponses({ @APIResponse(responseCode = "200", description = "Production plans returned successfully"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred while retrieving plans") })
    public List<ProductionPlanResponse> getPlans() {
        return productionPlanService.getAll();
    }
}