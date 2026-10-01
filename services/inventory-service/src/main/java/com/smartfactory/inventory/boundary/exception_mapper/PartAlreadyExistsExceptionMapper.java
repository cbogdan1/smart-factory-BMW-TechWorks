package com.smartfactory.inventory.boundary.exception_mapper;

import java.util.Map;
import com.smartfactory.inventory.control.exception.PartAlreadyExistsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class PartAlreadyExistsExceptionMapper implements ExceptionMapper<PartAlreadyExistsException>{
    @Override
    public Response toResponse(final PartAlreadyExistsException exception){
        return Response.status(Response.Status.CONFLICT).entity(Map.of("message", exception.getMessage())).build();
    }
}

