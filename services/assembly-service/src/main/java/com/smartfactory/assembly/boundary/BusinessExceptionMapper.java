package com.smartfactory.assembly.boundary;

import com.smartfactory.common.dto.assembly.ErrorResponse;
import com.smartfactory.common.exception.BusinessException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(final BusinessException exception) {
        final ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(ZoneOffset.UTC),
                exception.getStatusCode(),
                Response.Status.fromStatusCode(exception.getStatusCode()).getReasonPhrase(),
                exception.getMessage(),
                uriInfo.getRequestUri().getPath()
        );

        return Response.status(exception.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse)
                .build();
    }
}
