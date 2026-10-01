package com.smartfactory.procurement.boundary;

import java.util.Map;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.procurement.control.service.SimulateDeliveryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.SIMULATEDELIVERY)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulate Supplier Delivery", description = "Simulate the delivery for an supplier order operation.")
public class SimulateDeliveryResource {

    @Inject
    SimulateDeliveryService simulateDeliveryService;

    @Inject
    PartsDeliveredPublisher partsDeliveredPublisher;

    @POST
    @Path("/{id}/deliver")
    @Operation(summary = "Simulate Delivery", description = "Simulate delivery for a PO and publish to Kafka.")
    public Response simulateDelivery(final @PathParam("id") String purchaseOrderId) {

        final PartsDeliveredEvent event = simulateDeliveryService.simulateDelivery(purchaseOrderId, "BMW-001");

        partsDeliveredPublisher.publish(event);

        return Response.ok().entity(Map.of("message", "Delivery event published successfully")).build();
    }
}
