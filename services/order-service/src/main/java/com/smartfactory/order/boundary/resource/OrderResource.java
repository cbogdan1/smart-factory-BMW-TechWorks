package com.smartfactory.order.boundary.resource;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.order.CreateOrderRequest;
import com.smartfactory.common.dto.order.OrderResponse;
import com.smartfactory.common.dto.order.OrderStatusUpdateRequest;
import com.smartfactory.common.dto.order.OrderStatusUpdateResponse;
import com.smartfactory.common.dto.order.OrderViewResponse;
import com.smartfactory.order.control.order_service.OrderService;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.ORDERS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Orders", description = "Customer order management operations")
public class OrderResource {
    private final OrderService orderService;

    public OrderResource(final OrderService orderService) {
        this.orderService = orderService;
    }

    @POST
    @Operation(summary = "Create customer order", description = """
            Registers a new customer order.
            The system automatically generates the order identifier and
            vehicle identifier, creation date and assigns status CREATED.
            """)
    @APIResponse(responseCode = "201", description = "Order created successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = OrderResponse.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    examples = @ExampleObject(name = "Validation Error", value = """
                            {
                              "title": "Constraint Violation",
                              "status": 400,
                              "violations": [
                                {
                                  "field": "createOrder.request.customerName",
                                  "message": "must not be blank"
                                }
                              ]
                            }
                            """)))
    @APIResponse(responseCode = "422", description = """
            An invalid configuration order will be met with error code 422 and a message regarding whether \
            the model or the battery Type is invalid. \n
            valid configs: \
            BMW_I4 → [LONG_RANGE, STANDARD_RANGE]
            BMW_IX → [LONG_RANGE, PERFORMANCE]
            BMW_M5 → [STANDARD_RANGE]\s""")

    public Response createOrder(@Valid final CreateOrderRequest request) {

        final OrderResponse response = orderService.createOrder(request);

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve customer.")
    @APIResponse(responseCode = "200", description = "Order retrieve successful")
    @APIResponse(responseCode = "404", description = "Order not found")
    public Response getOrderById(@PathParam("id") final String orderId) {
        final OrderViewResponse dto = orderService.getOrderById(orderId);

        return Response.ok(dto).build();
    }

    @PUT
    @Path("/{id}/status")
    @Operation(summary = "Update order status",
            description = "Provide a string id and a status for the order you want to update.")
    @APIResponse(responseCode = "200", description = "Order status update successful")
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "409", description = "Order status not modified because of invalid request")
    @APIResponse(responseCode = "400", description = "Order status is null")
    public Response setOrderStatus(@PathParam("id") final String orderId,
            @Valid @NotNull final OrderStatusUpdateRequest status) {
        final OrderStatusUpdateResponse response = orderService.changeStatus(orderId, status.newStatus());
        return Response.ok(response).build();
    }
}