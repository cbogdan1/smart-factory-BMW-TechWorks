package com.smartfactory.dwh.boundary.exception;

import com.smartfactory.common.exception.BusinessException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(final BusinessException exception) {

        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), exception.getErrorCode());

        return Response.status(exception.getStatusCode()).entity(errorResponse).build();
    }
}
