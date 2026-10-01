package com.smartfactory.inventory.boundary;

import java.util.UUID;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.inventory.CreatePartRequest;
import com.smartfactory.common.dto.inventory.PartResponse;
import com.smartfactory.inventory.control.PartService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.PARTS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
@Tag(name = "Part Catalog", description = "Endpoints for managing factory parts")
public class PartResource {
    @Inject
    PartService partService;

    @POST
    @Operation(summary = "Create a new part", description = "Creates a new part in the inventory system")
    @APIResponse(responseCode = "201", description = "Part created successfully")
    @APIResponse(responseCode = "409", description = "Part with the same code already exists")
    @APIResponse(responseCode = "400", description = "Invalid request payload")
    public Response createPart(@Valid final CreatePartRequest request) {
        log.info("Received request to create part: {}", request.getPartCode());
        final PartResponse response = partService.createPart(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Operation(summary = "Get all parts", description = "Retrieves a list of all parts in the inventory system")
    @APIResponse(responseCode = "200", description = "List of parts retrieved successfully")
    public Response getAllParts() {
        return Response.ok(partService.getAllParts()).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get part by ID", description = "Retrieves a part by its ID from the inventory system")
    @APIResponse(responseCode = "200", description = "Part retrieved successfully")
    @APIResponse(responseCode = "404", description = "Part with the specified ID not found")
    public Response getPartById(@PathParam("id") final UUID id) {
        return Response.ok(partService.getPartById(id)).build();
    }

    @GET
    @Path("/code/{partCode}")
    @Operation(summary = "Get part by code",
            description = "Retrieves a part by its part code from the inventory system")
    @APIResponse(responseCode = "200", description = "Part retrieved successfully")
    @APIResponse(responseCode = "404", description = "Part with the specified code not found")
    public Response getPartByCode(@PathParam("partCode") final String partCode) {
        return Response.ok(partService.getPartByCode(partCode)).build();
    }
}
