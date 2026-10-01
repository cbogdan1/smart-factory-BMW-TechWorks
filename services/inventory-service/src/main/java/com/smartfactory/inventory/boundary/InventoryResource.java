package com.smartfactory.inventory.boundary;


import java.util.List;
import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.inventory.CreateUpdateInventoryStockRequest;
import com.smartfactory.common.dto.inventory.InventoryStockResponse;
import com.smartfactory.inventory.control.InventoryStockService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@Path(EndpointPaths.INVENTORY)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Inventory", description = "Inventory Management Endpoints")
@Slf4j
public class InventoryResource {

    @Inject
    InventoryStockService inventoryStockService;

    @PUT
    @Path("/{partId}")
    @Operation(summary = "Insert a new stock record/Update an existing stock record by partId", description =
            "Consumes a json containing the availableQuantity, reservedQuantity, minimumQuantity, maximumQuantity of said part."
                    + " If no record for said partId exists, it inserts a new record based on these values, otherwise, it updates an existing record.")
    @APIResponse(responseCode = "200", description = "Stock record updated/inserted successfully.")
    @APIResponse(responseCode = "400", description = "The request is invalid.")
    // @Valid is needed for the checks in CreateUpdateInventoryStockRequest DTO
    public Response updateStock(@PathParam("partId") final String partId,
            @Valid final CreateUpdateInventoryStockRequest request) {

        log.info("Received request to update stock for partId: {}", partId);

        final InventoryStockResponse response =
                inventoryStockService.updateStock(partId, request);

        return Response.ok(response).build();
    }

    @GET
    @Path("/{partId}")
    @APIResponse(responseCode = "200", description = "Inventory stock retrieved successfully.")
    @APIResponse(responseCode = "404", description = "Inventory stock not found.")
    public Response getStockForPart(@PathParam("partId") final String partId) {

        final InventoryStockResponse response =
                inventoryStockService.getStockForPart(partId);

        return Response.ok(response).build();
    }

    @GET
    @Path("/allInventoryStockRecords")
    @APIResponse(responseCode = "200", description = "All inventory stock recordds retrieved successfully.")
    public Response getAllStock() {

        final List<InventoryStockResponse> response =
                inventoryStockService.getAllStock();

        return Response.ok(response).build();
    }
}