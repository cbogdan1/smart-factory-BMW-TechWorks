package com.smartfactory.procurement.control.service.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import com.smartfactory.common.exception.BusinessException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(final BusinessException exception) {
        return Response.status(exception.getStatusCode())
                .entity(Map.of("message", exception.getMessage(), "errorCode", exception.getErrorCode(), "timestamp",
                        Instant.now().truncatedTo(ChronoUnit.SECONDS))).build();
    }
}
