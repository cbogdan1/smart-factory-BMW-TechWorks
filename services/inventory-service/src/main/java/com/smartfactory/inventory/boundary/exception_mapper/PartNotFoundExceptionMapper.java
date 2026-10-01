package com.smartfactory.inventory.boundary.exception_mapper;

import java.util.Map;
import com.smartfactory.inventory.control.exception.PartNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class PartNotFoundExceptionMapper implements ExceptionMapper<PartNotFoundException> {
    @Override
    public Response toResponse(final PartNotFoundException exception){
        return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", exception.getMessage())).build();
    }
}
