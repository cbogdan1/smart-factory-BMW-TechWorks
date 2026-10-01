package com.smartfactory.assembly.boundary;

import java.net.URI;
import java.util.List;

import com.smartfactory.assembly.control.services.VehicleProductionService;
import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.assembly.CreateVehicleProductionRequest;
import com.smartfactory.common.dto.assembly.ErrorResponse;
import com.smartfactory.common.dto.assembly.VehicleProductionResponse;
import com.smartfactory.common.dto.assembly.VehicleProductionView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.VEHICLES)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Vehicle Production", description = "Operations for managing vehicle production records")
public class VehicleProductionResource {

    private final VehicleProductionService service;

    public VehicleProductionResource(final VehicleProductionService service) {
        this.service = service;
    }

    @POST
    @Operation(summary = "Create a vehicle production record",
            description = "Creates a production record with generated identifiers " + "and default production values.")
    @APIResponse(responseCode = "201", description = "Vehicle production record created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = VehicleProductionResponse.class)))
    @APIResponse(responseCode = "400", description = "Invalid creation request")
    public Response create(
            @NotNull(message = "Request body must not be null") @Valid final CreateVehicleProductionRequest request) {
        final VehicleProductionResponse response = service.create(request);

        final URI location =
                UriBuilder.fromResource(VehicleProductionResource.class).path(response.vehicleId()).build();

        return Response.created(location).entity(response).build();
    }

    @GET
    @Path("/{vehicleId}")
    @Operation(summary = "Get a vehicle production record",
            description = "Returns a production record identified by vehicle ID.")
    @APIResponse(responseCode = "200", description = "Vehicle production record found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = VehicleProductionView.class)))
    @APIResponse(responseCode = "404", description = "Vehicle production record not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorResponse.class)))
    public VehicleProductionView findByVehicleId(@PathParam("vehicleId") final String vehicleId) {
        return service.findByVehicleId(vehicleId);
    }

    @GET
    @Operation(summary = "Get all vehicle production records",
            description = "Returns all existing vehicle production records.")
    @APIResponse(responseCode = "200", description = "Vehicle production records returned",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type = SchemaType.ARRAY, implementation = VehicleProductionResponse.class)))
    public List<VehicleProductionResponse> findAll() {
        return service.findAll();
    }

    @POST
    @Path("/{id}/advance")
    @Operation(summary = "Advance the production state of a vehicle production records.",
            description = "Advances the production state of an existing vehicle production records.")
    @APIResponse(responseCode = "200", description = "Vehicle production record state advanced successfully.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = VehicleProductionResponse.class)))
    @APIResponse(responseCode = "404", description = "Vehicle production record not found")
    @APIResponse(responseCode = "409", description = "Invalid production stage transition")
    @Consumes("application/json")
    @Produces("application/json")
    public Response advanceVehicleState(@PathParam("id") final String vehicleId) {
        final VehicleProductionResponse response = this.service.advanceStage(vehicleId);

        final URI location =
                UriBuilder.fromResource(VehicleProductionResource.class).path(response.vehicleId()).build();

        return Response.ok(response).location(location).build();
    }
}
