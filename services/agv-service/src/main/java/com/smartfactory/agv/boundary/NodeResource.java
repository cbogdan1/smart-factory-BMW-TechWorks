package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.NODES;

import java.util.List;

import com.smartfactory.agv.control.service.NodeService;
import com.smartfactory.common.dto.agv.CreateNodeRequest;
import com.smartfactory.common.dto.agv.NodeResponse;
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

@Path(NODES)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Node API", description = "Endpoints for managing factory locations (nodes).")
public class NodeResource {

    @Inject
    NodeService nodeService;

    @POST
    @Operation(summary = "Create a new node",
            description = "Add a new location to the routing topology (e.g., WAREHOUSE, BUFFER).")
    @APIResponses(value = { @APIResponse(responseCode = "201", description = "Node created successfully"),
            @APIResponse(responseCode = "400", description = "Validation error, invalid type or duplicate name") })
    public Response createNode(@Valid final CreateNodeRequest request) {
        final NodeResponse response = nodeService.createNode(request);
        return Response.status(Response.Status.CREATED).entity(response).build();

    }

    @GET
    @Operation(summary = "Return all nodes", description = "Fetches the complete list of existing locations.")
    @APIResponse(responseCode = "200", description = "List returned successfully")
    public Response getAllNodes() {
        final List<NodeResponse> nodes = nodeService.getAllNodes();
        return Response.ok(nodes).build();
    }

    @GET
    @Path("/{nodeId}")
    @Operation(summary = "Find a node by ID",
            description = "Returns the details of a single node using the business identifier (e.g., NODE-1234ABCD).")
    @APIResponses(value = { @APIResponse(responseCode = "200", description = "Node found"),
            @APIResponse(responseCode = "404", description = "Node not found") })
    public Response getNodeById(@PathParam("nodeId") final String nodeId) {
        final NodeResponse response = nodeService.getNodeById(nodeId);
        return Response.ok(response).build();
    }
}