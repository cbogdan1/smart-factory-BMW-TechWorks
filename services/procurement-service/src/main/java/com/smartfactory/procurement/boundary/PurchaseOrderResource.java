package com.smartfactory.procurement.boundary;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.procurement.CreatePurchaseOrderRequest;
import com.smartfactory.common.dto.procurement.CreatePurchaseOrderResponse;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineRequest;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineResponse;
import com.smartfactory.procurement.control.service.PurchaseOrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.PURCHASEORDER)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Purchase Order", description = "Purchase Order operations.")
public class PurchaseOrderResource {

    @Inject
    PurchaseOrderService purchaseOrderService;

    @POST
    @Operation(summary = "Register new purchase order.", description = "Create a new purchase order.")
    public Response createPurchaseOrder(final CreatePurchaseOrderRequest request) {
        final CreatePurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{purchaseOrderId}/status")
    @Operation(summary = "Change purchase order status",
            description = "Updates the status of an existing purchase order.")
    public Response changeStatus(@PathParam("purchaseOrderId") final String purchaseOrderId,
            final PurchaseOrderStateMachineRequest request) {

        final PurchaseOrderStateMachineResponse response = purchaseOrderService.changeStatus(purchaseOrderId, request);

        return Response.ok(response).build();
    }

}
