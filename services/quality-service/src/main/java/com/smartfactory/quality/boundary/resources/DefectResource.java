package com.smartfactory.quality.boundary.resources;

import java.util.List;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.quality.CreateDefectRequest;
import com.smartfactory.common.dto.quality.DefectResponse;
import com.smartfactory.quality.control.services.DefectService;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.DEFECTS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Defect", description = "Defect management endpoints")
public class DefectResource {

    @Inject
    DefectService defectService;

    @POST
    @Operation(summary = "Create a new defect",
            description = "Creates a new standardized defect. The defect code must be unique. The defect is automatically marked as active and the current creation date is assigned.")
    @APIResponse(responseCode = "201", description = "Defect created successfully.")
    @APIResponse(responseCode = "400", description = "The request is invalid.")
    @APIResponse(responseCode = "409", description = "Defect code already exists.")
    public Response createDefect(@Valid final CreateDefectRequest request) {
        final DefectResponse response = defectService.createDefect(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{code}")
    @Operation(summary = "Retrieve a defect by code",
            description = "Returns the defect associated with the provided defect code. If no defect exists with the given code, a 404 Not Found response is returned.")
    @APIResponse(responseCode = "200", description = "Defect retrieved successfully.")
    @APIResponse(responseCode = "404", description = "Defect not found.")
    public Response findByCode(@PathParam("code") final String code) {
        final DefectResponse response = defectService.findByCode(code);
        return Response.ok(response).build();
    }

    @GET
    @Operation(summary = "Retrieve all defects",
            description = "Returns a list of all standardized defects currently stored in the system.")
    @APIResponse(responseCode = "200", description = "List of defects returned successfully.")
    public Response findAll() {
        final List<DefectResponse> response = defectService.findAll();
        return Response.ok(response).build();
    }
}