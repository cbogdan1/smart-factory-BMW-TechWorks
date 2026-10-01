package com.smartfactory.quality.boundary.resources;

import java.util.List;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.quality.AddDefectToInspectionRequest;
import com.smartfactory.common.dto.quality.CreateInspectionRequest;
import com.smartfactory.common.dto.quality.InspectionDecisionResponse;
import com.smartfactory.common.dto.quality.InspectionDefectResponse;
import com.smartfactory.common.dto.quality.InspectionResponse;
import com.smartfactory.common.dto.quality.InspectionScoreResponse;
import com.smartfactory.common.dto.quality.InspectionView;
import com.smartfactory.common.enums.Decision;
import com.smartfactory.quality.control.services.InspectionService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(EndpointPaths.INSPECTIONS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Inspection", description = "Inspection management endpoints")
public class InspectionResource {

    @Inject
    InspectionService inspectionService;

    @POST
    @Operation(summary = "Create a new inspection",
            description = "Creates a new inspection for the specified vehicle. A unique inspection ID is generated automatically, the current date and time are assigned, and the initial status is set to PENDING.")
    @APIResponse(responseCode = "201", description = "Inspection created successfully.")
    @APIResponse(responseCode = "400", description = "The request is invalid.")
    public Response createInspection(@Valid final CreateInspectionRequest request) {
        final InspectionResponse response = inspectionService.createInspection(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/{inspectionId}/defects")
    @Operation(summary = "Attach a defect to an inspection",
            description = "Links a standardized defect to the given inspection, with an optional comment. "
                    + "The same defect cannot be linked twice to the same inspection.")
    @APIResponse(responseCode = "201", description = "Defect linked to the inspection successfully.")
    @APIResponse(responseCode = "400", description = "The request is invalid.")
    @APIResponse(responseCode = "404", description = "Inspection not found.")
    @APIResponse(responseCode = "409", description = "This defect is already linked to the inspection.")
    @APIResponse(responseCode = "422", description = "The defect code is not known.")
    public Response addDefectToInspection(@PathParam("inspectionId") final String inspectionId,
            @Valid final AddDefectToInspectionRequest request) {
        final InspectionDefectResponse response = inspectionService.addDefectToInspection(inspectionId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/{inspectionId}/calculate")
    @Operation(summary = "Calculate the quality score of an inspection", description =
            "Computes the quality score of the given inspection as 100 minus the sum of the penalty points of "
                    + "every defect registered during that inspection, and stores the result on the inspection. "
                    + "An inspection without defects scores 100; a score can never go below 0. "
                    + "Calling this endpoint again recalculates and overwrites the previous score. "
                    + "If no inspection exists with the given ID, a 404 Not Found response is returned.")
    @APIResponse(responseCode = "200", description = "Score calculated successfully.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = InspectionScoreResponse.class)))
    @APIResponse(responseCode = "404", description = "Inspection not found.")
    public InspectionScoreResponse calculateScore(@PathParam("inspectionId") final String inspectionId) {
        return inspectionService.calculateScore(inspectionId);
    }

    @GET
    @Path("/{inspectionId}")
    @Operation(summary = "Retrieve a complete inspection report by ID", description =
            "Returns the inspection associated with the provided inspection ID together with every defect "
                    + "registered during that inspection. For each defect the standardized defect code, description and "
                    + "penalty points are returned, along with the comment written by the inspector. "
                    + "If no inspection exists with the given ID, a 404 Not Found response is returned.")
    @APIResponse(responseCode = "200", description = "Inspection retrieved successfully.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = InspectionView.class)))
    @APIResponse(responseCode = "404", description = "Inspection not found.")
    public InspectionView findById(@PathParam("inspectionId") final String inspectionId) {
        return inspectionService.findById(inspectionId);
    }

    @GET
    @Path("/{inspectionId}/decision")
    @Operation(summary = "Retrieve the automatic decision of an inspection",
            description = "Returns the decision stored for the given inspection: PASS when the score is 80 or higher, "
                    + "REWORK from 50 to 79, FAIL below 50. For a REWORK the earliest production stage that "
                    + "must be redone is returned as well. Both fields are null while the score has never been "
                    + "calculated. If no inspection exists with the given ID, a 404 Not Found response is returned.")
    @APIResponse(responseCode = "200", description = "Decision retrieved successfully.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = InspectionDecisionResponse.class)))
    @APIResponse(responseCode = "404", description = "Inspection not found.")
    public InspectionDecisionResponse findDecisionById(@PathParam("inspectionId") final String inspectionId) {
        return inspectionService.findDecisionById(inspectionId);
    }

    @GET
    @Operation(summary = "Retrieve all inspections",
            description = "Returns a list of all inspections currently stored in the system.")
    @APIResponse(responseCode = "200", description = "List of inspections returned successfully.")
    public List<InspectionResponse> findAll() {
        return inspectionService.findAll();
    }

    @GET
    @Path("/history")
    @Operation(summary = "Retrieve inspection history",
            description = "Returns all inspections with their defects and decisions. Supports optional ?vehicleId and ?decision filters.")
    @APIResponse(responseCode = "200", description = "List of inspection views returned successfully.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = InspectionView.class, type = SchemaType.ARRAY)))
    public List<InspectionView> history(@Parameter(description = "Filter by vehicle id", required = false) @QueryParam(
                    "vehicleId") final String vehicleId,
            @Parameter(description = "Filter by decision (PASS, REWORK, FAIL)", required = false) @QueryParam(
                    "decision") final Decision decision) {
        return inspectionService.history(vehicleId, decision);
    }
}