package com.smartfactory.dwh.boundary.resource;

import static com.smartfactory.common.EndpointPaths.ANALYTICS_SUMMARY;

import com.smartfactory.dwh.control.service.AnalyticsService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path(ANALYTICS_SUMMARY)
@Produces(MediaType.APPLICATION_JSON)
public class AnalyticsResource {

    private final AnalyticsService analyticsService;

    public AnalyticsResource(final AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GET
    @Operation(summary = "Retrieve analytic summary",
            description = "Retrieve analytic summary of the data inside data warehouse. ")
    @APIResponse(responseCode = "200", description = "Analytic summary retrieved successfully")
    @APIResponse(responseCode = "500", description = "Failed to build analytics summary")
    public Response getAnalyticSummary() {
        return Response.ok(analyticsService.getAnalyticsSummary()).build();
    }
}
