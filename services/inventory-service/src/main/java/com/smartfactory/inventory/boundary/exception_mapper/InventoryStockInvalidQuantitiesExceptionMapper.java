package com.smartfactory.inventory.boundary.exception_mapper;

import java.util.Map;

import com.smartfactory.inventory.control.exception.InventoryStockInvalidQuantitiesException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InventoryStockInvalidQuantitiesExceptionMapper implements ExceptionMapper<InventoryStockInvalidQuantitiesException> {
    @Override
    public Response toResponse(final InventoryStockInvalidQuantitiesException exception){
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", exception.getMessage())).build();
    }
}
