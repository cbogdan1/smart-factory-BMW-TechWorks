package com.smartfactory.procurement.control;

import com.smartfactory.common.EndpointPaths;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "inventory-api")
@Path(EndpointPaths.PARTS)
public interface InventoryClient {
    @GET
    @Path("/{partId}")
    Response getPartById(@PathParam("partId") String partId);
}
