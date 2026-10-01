package com.smartfactory.inventory.boundary.exception_mapper;

import java.util.Map;
import com.smartfactory.inventory.control.exception.InventoryStockNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InventoryStockNotFoundExceptionMapper implements ExceptionMapper<InventoryStockNotFoundException> {
    @Override
    public Response toResponse(final InventoryStockNotFoundException exception){
        return Response.status(Response.Status.NOT_FOUND).entity(Map.of("message", exception.getMessage())).build();
    }
}
