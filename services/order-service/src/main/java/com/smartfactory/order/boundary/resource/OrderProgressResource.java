package com.smartfactory.order.boundary.resource;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.order.OrderProgressView;
import com.smartfactory.order.control.order_service.OrderProgressService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path(EndpointPaths.ORDERS)
@Produces(MediaType.APPLICATION_JSON)
public class OrderProgressResource {

    @Inject
    OrderProgressService service;

    @GET
    @Path("/{orderId}/progress")
    @Operation(summary = "Get order progress",
            description = "Returns status, factory stage and completion information for an order")
    @APIResponse(responseCode = "200", description = "Order progress found")
    @APIResponse(responseCode = "404", description = "Order not found")
    public OrderProgressView getOrderProgress(@PathParam("orderId") String orderId) {
        return service.getProgressView(orderId);
    }
}
