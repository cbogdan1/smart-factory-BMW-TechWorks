package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.ROUTES;

import com.smartfactory.agv.control.service.RouteCalculatorService;
import com.smartfactory.common.dto.agv.CreateCalculateRouteRequest;
import com.smartfactory.common.dto.agv.RouteResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(ROUTES)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Route API")
public class RouteResource {

    @Inject
    RouteCalculatorService routeCalculatorService;

    @POST
    @Path("/calculate")
    @Operation(summary = "Calculate shortest route",
            description = "Uses Dijkstra to calculate the minimum-distance path.")
    @APIResponses({ @APIResponse(responseCode = "200", description = "Shortest path found"),
            @APIResponse(responseCode = "422", description = "No route available"),
            @APIResponse(responseCode = "400", description = "Source and target nodes are required") })
    public RouteResponse calculateRoute(@Valid final CreateCalculateRouteRequest request) {
        return routeCalculatorService.calculateRoute(request);
    }
}
