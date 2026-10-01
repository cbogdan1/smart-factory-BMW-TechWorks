package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.ASSIGNMENTS;

import com.smartfactory.agv.control.service.AgvAssignmentService;
import com.smartfactory.common.dto.agv.AssignAgvRequest;
import com.smartfactory.common.dto.agv.AssignAgvResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(ASSIGNMENTS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "AGV Assignment API", description = "Endpoints for managing AGV assignments to delivery missions.")
public class AgvAssignmentResource {

    @Inject
    AgvAssignmentService agvAssignmentService;

    @POST
    @Operation(summary = "Assign the closest available AGV",
            description = "Selects the AVAILABLE AGV whose currentNode is closest (minimum Dijkstra distance) to the sourceNodeId of the delivery. Ignores AGVs with status BUSY or MAINTENANCE.")
    @APIResponses(
            value = { @APIResponse(responseCode = "200", description = "Closest available AGV assigned successfully"),
                    @APIResponse(responseCode = "400",
                            description = "Validation error, missing source or target node IDs"),
                    @APIResponse(responseCode = "409", description = "No available AGV in the fleet"),
                    @APIResponse(responseCode = "422",
                            description = "No route available from any available AGV to the target node") })
    public Response assignAgv(@Valid final AssignAgvRequest request) {
        final AssignAgvResponse response = agvAssignmentService.assignAgv(request);
        return Response.ok(response).build();
    }
}