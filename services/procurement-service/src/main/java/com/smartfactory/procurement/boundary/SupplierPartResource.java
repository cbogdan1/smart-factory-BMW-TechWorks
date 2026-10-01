package com.smartfactory.procurement.boundary;

import java.util.List;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.procurement.CreateSupplierPartRequest;
import com.smartfactory.common.dto.procurement.SupplierPartResponse;
import com.smartfactory.common.dto.procurement.SuppliersForPartResponse;
import com.smartfactory.procurement.control.service.SupplierPartService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.SUPPLIERPART)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Supplier Parts", description = "Endpoints for managing and sourcing supplier-part relationships")
public class SupplierPartResource {
    @Inject
    SupplierPartService service;

    @GET
    @Operation(summary = "Get all supplier-part relationships",
            description = "Retrieves a complete list of all supplier and part relationships.")
    public Response getAllSupplierParts() {
        final List<SupplierPartResponse> relationships = service.getAllSupplierParts();
        return Response.ok(relationships).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a specific relationship by ID",
            description = "Retrieves a single supplier-part relationship using it's primary key.")
    public Response getSupplierPartById(@PathParam("id") final Long id) {
        final SupplierPartResponse relationship = service.getSupplierPartById(id);
        return Response.ok(relationship).build();
    }

    @POST
    @Operation(summary = "Record which supplier provides which part",
            description = "Create a new supplier-part relationship after validating supplier and part existence.")
    public Response createSupplierPart(final CreateSupplierPartRequest request) {
        final SupplierPartResponse response = service.createRelationship(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{partId}/suppliers")
    @Operation(summary = "Get all suppliers that sell a part",
            description = "Retrieves all suppliers that sell ordered by lowest lead time.")
    public Response getSuppliersForGivenPart(@PathParam("partId") final String partId) {
        final List<SuppliersForPartResponse> suppliers = service.getSuppliersOrderedByLeadTimeAsc(partId);
        return Response.ok(suppliers).build();
    }
}
