package com.smartfactory.procurement.boundary;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.procurement.CreateSupplierRequest;
import com.smartfactory.common.dto.procurement.SupplierResponse;
import com.smartfactory.common.dto.procurement.SupplierSelectionResponse;
import com.smartfactory.procurement.control.service.SupplierSelectionService;
import com.smartfactory.procurement.control.service.SupplierService;
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
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.SUPPLIERS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Suppliers", description = "Procurement operations for managing suppliers")
public class SupplierResource {

    @Inject
    SupplierService supplierService;

    @Inject
    SupplierSelectionService supplierSelectionService;

    @POST
    @Operation(summary = "Register a new supplier",
            description = "Creates a new supplier and assigns a SUP- identifier")
    @APIResponse(responseCode = "201", description = "Supplier successfully created",
            content = @Content(schema = @Schema(implementation = SupplierResponse.class)))
    @APIResponse(responseCode = "400", description = "Validation error or duplicate error")
    public Response createSupplier(@Valid final CreateSupplierRequest createSupplierRequest) {
        final SupplierResponse supplierResponseDTO = supplierService.createSupplier(createSupplierRequest);
        return Response.status(Response.Status.CREATED).entity(supplierResponseDTO).build();
    }

    @GET
    @Operation(summary = "Get all suppliers", description = "Retrieves a full list of registered suppliers")
    @APIResponse(responseCode = "200", description = "Successful retrieval")
    public Response getAllSuppliers() {
        return Response.ok(supplierService.getAllSuppliers()).build();
    }

    @GET
    @Path("/{supplierId}")
    @Operation(summary = "Get supplier by ID", description = "Retrieves a single supplier using their SUP- identifier.")
    @APIResponse(responseCode = "200", description = "Supplier found",
            content = @Content(schema = @Schema(implementation = SupplierResponse.class)))
    @APIResponse(responseCode = "404", description = "Supplier not found")
    public Response getSupplierById(final @PathParam("supplierId") String supplierId) {
        return Response.ok(supplierService.getSupplierById(supplierId)).build();
    }

    @GET
    @Path("/select-best/{partId}")
    @Operation(summary = "Select best supplier for a part", description = "Retrieves the supplier with the lowest lead"
            + "time days. Ties are broken by highest rating, then lowest unit cost.")
    @APIResponse(responseCode = "200", description = "Best supplier selected.",
            content = @Content(schema = @Schema(implementation = SupplierSelectionResponse.class)))
    @APIResponse(responseCode = "422", description = "No supplier available for part.")
    public Response selectBestSupplier(final @PathParam("partId") String partId) {
        final SupplierSelectionResponse response = supplierSelectionService.selectBest(partId);
        return Response.ok(response).build();
    }

}
