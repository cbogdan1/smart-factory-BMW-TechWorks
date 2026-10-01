package com.smartfactory.dwh.boundary.resource;

import static com.smartfactory.common.EndpointPaths.DASHBOARD_KPIS;

import com.smartfactory.dwh.control.service.KpiService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path(DASHBOARD_KPIS)
@Produces(MediaType.APPLICATION_JSON)
public class KpiResource {
    private final KpiService kpiService;

    public KpiResource(final KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @GET
    @Operation(summary = "Retrieve dashboard KPIs",
            description = "Retrieved the dashboard KPIs for the data inside data warehouse")
    @APIResponse(responseCode = "200", description = "Dashboard KPIs retrieved successfully")
    @APIResponse(responseCode = "500", description = "Failed to build the dashboard KPIs")
    public Response getDashboardKpis() {
        return Response.ok(kpiService.getDashboardKpis()).build();
    }
}
