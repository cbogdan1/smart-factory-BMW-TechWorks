package com.smartfactory.inventory.boundary;

import java.util.List;
import com.smartfactory.common.dto.inventory.ReservationResponse;
import com.smartfactory.inventory.control.ReservationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/reservations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
@Tag(name = "Reservations", description = "Material Reservation Endpoints")
public class ReservationResource {

    @Inject
    ReservationService reservationService;

    @GET
    @Operation(summary = "Get material reservations by vehicle ID",
            description = "Retrieves all material reservation records associated with the specified vehicle ID.")
    @APIResponse(responseCode = "200", description = "Reservations retrieved successfully.")
    public List<ReservationResponse> getReservationsByVehicleId(@QueryParam("vehicleId") final String vehicleId) {
        return reservationService.getReservationsByVehicleId(vehicleId);
    }

    @POST
    @Path("/{reservationId}/consume")
    @Produces(MediaType.APPLICATION_JSON)
    public ReservationResponse consumeReservation(@PathParam("reservationId") final String reservationId) {
        log.info("Received request to consume reservation: {}", reservationId);
        return reservationService.consumeReservation(reservationId);
    }

}