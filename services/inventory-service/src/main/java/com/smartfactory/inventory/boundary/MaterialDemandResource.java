package com.smartfactory.inventory.boundary;

import java.util.List;
import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.inventory.MaterialDemandResponse;
import com.smartfactory.inventory.control.MaterialDemandService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.MATERIAL_DEMANDS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Material Demand", description = "Material Demand Management Endpoints")
public class MaterialDemandResource {

    @Inject
    MaterialDemandService materialDemandService;

    @GET
    @Path("/getUpcomingMaterialDemands")
    @Operation(
            summary = "Get all upcoming material demands",
            description = "Retrieves all material demand records with an upcoming planned production date."
    )
    @APIResponse(
            responseCode = "200",
            description = "Upcoming material demands retrieved successfully."
    )
    public Response getUpcomingDemands() {
        final List<MaterialDemandResponse> demands =
                materialDemandService.getUpcomingDemands();

        return Response.ok(demands).build();
    }

    @GET
    @Path("/getAllMaterialDemands")
    @Operation(
            summary = "Get all material demands",
            description = "Retrieves all material demand records"
    )
    @APIResponse(
            responseCode = "200",
            description = "Material demands retrieved successfully."
    )
    public Response getAllMaterialDemands() {
        final List<MaterialDemandResponse> demands =
                materialDemandService.getAllMaterialDemands();

        return Response.ok(demands).build();
    }

    @GET
    @Path("/vehicle/{vehicleId}")
    @Operation(
            summary = "Get material demands for a vehicle",
            description = "Retrieves all material demand records associated with the specified vehicle."
    )
    @APIResponse(
            responseCode = "200",
            description = "Material demands retrieved successfully."
    )
    public Response getDemandsByVehicleId(
            @PathParam("vehicleId") final String vehicleId
    ) {
        final List<MaterialDemandResponse> demands =
                materialDemandService.getDemandsByVehicleId(vehicleId);

        return Response.ok(demands).build();
    }

    @GET @Path("/plan/{planId}")
    @Operation( summary = "Get material demands for a plan", description = "Retrieves all material demand records associated with the specified production plan." )
    @APIResponse( responseCode = "200", description = "Material demands retrieved successfully." )
    public Response getDemandsByPlanId( @PathParam("planId") final String planId ) {
        final List<MaterialDemandResponse> demands = materialDemandService.getByPlanId(planId);
        return Response.ok(demands).build();
    }
}

