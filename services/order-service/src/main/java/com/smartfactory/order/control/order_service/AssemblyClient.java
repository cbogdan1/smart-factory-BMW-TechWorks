package com.smartfactory.order.control.order_service;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.assembly.VehicleProductionView;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "assembly-api")
@Path(EndpointPaths.VEHICLES)
public interface AssemblyClient {

    @GET
    @Path("/{vehicleId}")
    VehicleProductionView findByVehicleId(@PathParam("vehicleId") String vehicleId);
}
