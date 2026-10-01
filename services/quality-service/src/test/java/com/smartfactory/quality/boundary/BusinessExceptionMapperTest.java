package com.smartfactory.quality.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.quality.boundary.utils.BusinessExceptionMapper;
import com.smartfactory.quality.boundary.utils.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

public class BusinessExceptionMapperTest {

    private final BusinessExceptionMapper mapper = new BusinessExceptionMapper();

    @Test
    public void defectNotFoundIsMappedTo404() {
        final BusinessException exception =
                new BusinessException("Defect with code 'UNKNOWN' not found", "DEFECT_NOT_FOUND", 404);

        final Response response = mapper.toResponse(exception);
        final ErrorResponse body = (ErrorResponse) response.getEntity();

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNotNull(body);
        assertEquals("DEFECT_NOT_FOUND", body.code());
        assertEquals("Defect with code 'UNKNOWN' not found", body.message());
        assertNotNull(body.timestamp());
    }

    @Test
    public void inspectionNotFoundIsMappedTo404() {
        final BusinessException exception =
                new BusinessException("Inspection 'INSP-999' not found", "INSPECTION_NOT_FOUND", 404);

        final Response response = mapper.toResponse(exception);
        final ErrorResponse body = (ErrorResponse) response.getEntity();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("INSPECTION_NOT_FOUND", body.code());
    }

    @Test
    public void defectAlreadyExistsIsMappedTo409() {
        final BusinessException exception =
                new BusinessException("Defect with code 'PAINT_SCRATCH' already exists", "DEFECT_ALREADY_EXISTS", 409);

        final Response response = mapper.toResponse(exception);
        final ErrorResponse body = (ErrorResponse) response.getEntity();

        assertNotNull(response);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("DEFECT_ALREADY_EXISTS", body.code());
    }

    @Test
    public void defectAlreadyLinkedIsMappedTo409() {
        final BusinessException exception =
                new BusinessException("Defect 'DOOR_ALIGNMENT' is already linked to inspection 'INSP-007'",
                        "DEFECT_ALREADY_LINKED", 409);

        final Response response = mapper.toResponse(exception);
        final ErrorResponse body = (ErrorResponse) response.getEntity();

        assertEquals(409, response.getStatus());
        assertEquals("DEFECT_ALREADY_LINKED", body.code());
    }

    @Test
    public void invalidDefectCodeIsMappedTo400() {
        final BusinessException exception =
                new BusinessException("Defect with code 'NOT_A_REAL_CODE' not found", "INVALID_DEFECT_CODE", 400);

        final Response response = mapper.toResponse(exception);
        final ErrorResponse body = (ErrorResponse) response.getEntity();

        assertEquals(400, response.getStatus());
        assertEquals("INVALID_DEFECT_CODE", body.code());
    }

    @Test
    public void statusCodeComesFromTheException() {
        final BusinessException exception = new BusinessException("No AGV available", "NO_AGV_AVAILABLE", 503);

        assertEquals(503, mapper.toResponse(exception).getStatus());
    }
}