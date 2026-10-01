package com.smartfactory.dwh.boundary.resource;

import static com.smartfactory.common.EndpointPaths.EVENTS;

import com.smartfactory.dwh.control.service.EventStoreService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path(EVENTS)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    private final EventStoreService eventStoreService;

    public EventResource(final EventStoreService eventStoreService) {
        this.eventStoreService = eventStoreService;
    }

    @GET
    @Operation(summary = "Retrieve stored events", description = "Retrieves stored events from newest to oldest ")
    @APIResponse(responseCode = "200", description = "Events retrieved successfully")
    public Response getEvents() {
        return Response.ok(eventStoreService.getAllEvents()).build();
    }

}