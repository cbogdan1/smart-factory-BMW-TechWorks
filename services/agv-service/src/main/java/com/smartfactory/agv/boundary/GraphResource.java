package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.GRAPH;

import com.smartfactory.agv.control.service.GraphService;
import com.smartfactory.common.dto.agv.FactoryGraphResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(GRAPH)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Factory Graph API", description = "Endpoints for factory topology visualization.")
public class GraphResource {

    @Inject
    GraphService graphService;

    @GET
    @Operation(summary = "Get complete factory graph",
            description = "Returns nodes, edges and AGVs in a single response.")
    @APIResponse(responseCode = "200", description = "Factory graph returned successfully")
    public FactoryGraphResponse getGraph() {
        return graphService.getFactoryGraph();
    }
}