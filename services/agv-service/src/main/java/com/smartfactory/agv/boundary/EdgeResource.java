package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.EDGES;

import java.util.List;

import com.smartfactory.agv.control.service.EdgeService;
import com.smartfactory.common.dto.agv.CreateEdgeRequest;
import com.smartfactory.common.dto.agv.EdgeResponse;
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

@Path(EDGES)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Edge API", description = "Endpoints for managing paths (edges) between factory locations.")
public class EdgeResource {

    @Inject
    EdgeService edgeService;

    @POST
    @Operation(summary = "Create a new edge", description = "Connects two nodes with a specific distance in meters.")
    @APIResponses(value = { @APIResponse(responseCode = "201", description = "Edge created successfully"),
            @APIResponse(responseCode = "400", description = "Validation error or invalid distance"),
            @APIResponse(responseCode = "404", description = "Source or target node not found") })
    public Response createEdge(@Valid final CreateEdgeRequest request) {
        final EdgeResponse response = edgeService.createEdge(request);
        return Response.status(Response.Status.CREATED).entity(response).build();

    }

    @GET
    @Operation(summary = "Return all edges", description = "Fetches the complete list of existing paths/edges.")
    @APIResponse(responseCode = "200", description = "List returned successfully")
    public Response getAllEdges() {
        final List<EdgeResponse> edges = edgeService.getAllEdges();
        return Response.ok(edges).build();
    }

    @GET
    @Path("/{edgeId}")
    @Operation(summary = "Find an edge by ID",
            description = "Returns the details of a single edge using its unique identifier.")
    @APIResponses(value = { @APIResponse(responseCode = "200", description = "Edge found"),
            @APIResponse(responseCode = "404", description = "Edge not found") })
    public Response getEdgeById(@PathParam("edgeId") final String edgeId) {
        final EdgeResponse response = edgeService.getEdgeById(edgeId);
        return Response.ok(response).build();
    }
}