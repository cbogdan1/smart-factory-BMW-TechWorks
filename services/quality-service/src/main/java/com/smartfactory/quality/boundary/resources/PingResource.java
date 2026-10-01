package com.smartfactory.quality.boundary.resources;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Minimal readiness ping — a <b>Boundary</b> component.
 * Real health is auto-provided at {@code /q/health}.
 */
@Path("/api/ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {

    @GET
    public Map<String, String> ping() {
        return Map.of("service", "quality-service", "status", "UP");
    }
}