package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.AGVS;

import java.util.List;

import com.smartfactory.agv.control.service.AgvService;
import com.smartfactory.common.dto.agv.AgvResponse;
import com.smartfactory.common.dto.agv.CreateAgvRequest;

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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(AGVS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "AGV API", description = "Endpoints for managing Automated Guided Vehicles.")
public class AgvResource {

    @Inject
    AgvService agvService;

    @POST
    @Operation(summary = "Create a new AGV", description = "Creates a new AGV and assigns it to an existing node.")
    @APIResponses(value = { @APIResponse(responseCode = "201", description = "AGV created successfully"),
            @APIResponse(responseCode = "400", description = "Invalid request or referenced node does not exist") })
    public Response createAgv(@Valid final CreateAgvRequest request) {
        final AgvResponse response = agvService.createAgv(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Operation(summary = "Return all AGVs", description = "Fetches the complete list of AGVs available in the system.")
    @APIResponse(responseCode = "200", description = "List returned successfully")
    public List<AgvResponse> getAllAgvs() {
        return agvService.getAllAgvs();
    }

    @GET
    @Path("/{agvId}")
    @Operation(summary = "Find an AGV by ID",
            description = "Returns the details of a single AGV using its business identifier (e.g. AGV-1754161338123).")
    @APIResponses(value = { @APIResponse(responseCode = "200", description = "AGV found"),
            @APIResponse(responseCode = "404", description = "AGV not found") })
    public AgvResponse getAgv(@PathParam("agvId") final String agvId) {
        return agvService.getAgv(agvId);
    }
}