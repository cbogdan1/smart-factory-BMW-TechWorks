package com.smartfactory.dwh.boundary.resource;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.dwh.VehicleTwinResponse;
import com.smartfactory.dwh.control.service.VehicleTwinService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path(EndpointPaths.TWINS)
@Produces(MediaType.APPLICATION_JSON)
public class VehicleTwinResource {

    private final VehicleTwinService vehicleTwinService;

    public VehicleTwinResource(final VehicleTwinService vehicleTwinService) {
        this.vehicleTwinService = vehicleTwinService;
    }

    @GET
    @Path("/{vehicleId}")
    @Operation(summary = "Get vehicle digital twin",
            description = "Retrieves stored digital state for a given vehicleId")
    @APIResponse(responseCode = "200", description = "Vehicle digital twin retrieved successfully")
    @APIResponse(responseCode = "404", description = "Invalid vehicle identifier supplied")
    public Response getVehicleTwin(@PathParam("vehicleId") final String vehicleId) {
        final VehicleTwinResponse twin = vehicleTwinService.getVehicleTwin(vehicleId);
        return Response.ok(twin).build();
    }
}
