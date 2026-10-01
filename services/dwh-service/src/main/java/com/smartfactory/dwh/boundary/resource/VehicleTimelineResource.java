package com.smartfactory.dwh.boundary.resource;

import java.util.List;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.dwh.VehicleTimelineResponse;
import com.smartfactory.dwh.control.service.VehicleTimelineService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 * REST resource for managing and retrieving vehicle event timelines.
 * Provides endpoints to query historical events associated with specific vehicle IDs.
 */
@Path(EndpointPaths.TIMELINE)
@Produces(MediaType.APPLICATION_JSON)
public class VehicleTimelineResource {

    private final VehicleTimelineService vehicleTimelineService;

    public VehicleTimelineResource(final VehicleTimelineService vehicleTimelineService) {
        this.vehicleTimelineService = vehicleTimelineService;
    }

    @GET
    @Path("/{vehicleId}")
    @Operation(summary = "Get vehicle timeline",
            description = "Retrieves stored events for a specific vehicle ordered chronologically")
    @APIResponse(responseCode = "200", description = "Vehicle timeline retrieved successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = VehicleTimelineResponse.class)))
    @APIResponse(responseCode = "404", description = "Vehicle timeline not found for given vehicleId")
    public Response getVehicleTimeline(@PathParam("vehicleId") final String vehicleId) {
        final List<VehicleTimelineResponse> timeline = vehicleTimelineService.getVehicleTimeline(vehicleId);
        return Response.ok(timeline).build();
    }
}
