package com.smartfactory.quality.boundary.resources;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.quality.entity.engine.DemoOutcomeControl;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Demo control endpoint: pins the outcome of the automatic inspections so the happy-path,
 * rework and failure loops can be demonstrated deterministically.
 */
@Path(EndpointPaths.INSPECTIONS + "/demo/outcome")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Demo control", description = "Deterministic control of automatic inspection outcomes")
public class DemoOutcomeResource {

    @Inject
    DemoOutcomeControl demoOutcomeControl;

    @GET
    @Operation(summary = "Show the currently forced inspection outcome",
            description = "Returns RANDOM when the normal probabilistic defect seeding is active.")
    @APIResponse(responseCode = "200", description = "Current forced outcome returned.")
    public Response current() {
        return Response.ok(new OutcomeView(demoOutcomeControl.getForcedOutcome().name())).build();
    }

    @PUT
    @Path("/{outcome}")
    @Operation(summary = "Force the outcome of the next automatic inspections", description =
            "Accepts RANDOM, CLEAN, PASS, REWORK or FAIL. The value is sticky until it is changed again. "
                    + "CLEAN seeds no defects at all (score 100); PASS, REWORK and FAIL seed a defect "
                    + "combination from the catalog whose penalty points produce that decision.")
    @APIResponse(responseCode = "200", description = "Forced outcome updated.")
    @APIResponse(responseCode = "400", description = "Unknown outcome value.")
    public Response force(@PathParam("outcome") final String outcome) {
        final DemoOutcomeControl.Outcome parsed;
        try {
            parsed = DemoOutcomeControl.Outcome.valueOf(outcome.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new OutcomeView(
                    "Unknown outcome '" + outcome + "'. Expected one of RANDOM, CLEAN, PASS, REWORK, FAIL.")).build();
        }

        demoOutcomeControl.setForcedOutcome(parsed);

        return Response.ok(new OutcomeView(parsed.name())).build();
    }

    /**
     * Small response wrapper so the endpoint returns JSON instead of a bare string.
     */
    public record OutcomeView(String outcome) {
    }
}

