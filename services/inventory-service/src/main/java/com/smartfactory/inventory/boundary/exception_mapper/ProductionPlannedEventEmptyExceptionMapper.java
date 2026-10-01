package com.smartfactory.inventory.boundary.exception_mapper;

import java.util.Map;
import com.smartfactory.inventory.control.exception.ProductionPlannedEventEmptyException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProductionPlannedEventEmptyExceptionMapper implements ExceptionMapper<ProductionPlannedEventEmptyException> {
    @Override
    public Response toResponse(final ProductionPlannedEventEmptyException exception){
        return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", exception.getMessage())).build();
    }
}
