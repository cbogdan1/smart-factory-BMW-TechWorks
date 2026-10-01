package com.smartfactory.assembly.boundary;

import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_STAGE_TRANSITION_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_STAGE_TRANSITION_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.net.URI;

import com.smartfactory.common.dto.assembly.ErrorResponse;
import com.smartfactory.common.exception.BusinessException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusinessExceptionMapperTest {

    @Mock
    UriInfo uriInfo;

    @InjectMocks
    BusinessExceptionMapper mapper;

    private static final String REQUEST_PATH = "/api/vehicles/VEH-001/advance";

    @BeforeEach
    void setUp() {
        when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost:8085" + REQUEST_PATH));
    }

    @Test
    void shouldMapBusinessExceptionWithCustomStatusCodeToResponse() {
        final String expectedMessage = INVALID_STAGE_TRANSITION_ERROR_MESSAGE;
        final String expectedErrorCode = INVALID_STAGE_TRANSITION_ERROR_CODE;
        final int expectedStatus = Response.Status.CONFLICT.getStatusCode(); // 409

        final BusinessException exception =
                new BusinessException(expectedMessage, expectedErrorCode, expectedStatus);

        try (final Response response = mapper.toResponse(exception)) {
            assertEquals(expectedStatus, response.getStatus());
            assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

            final ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.timestamp());
            assertEquals(expectedStatus, errorResponse.status());
            assertEquals("Conflict", errorResponse.error());
            assertEquals(expectedMessage, errorResponse.message());
            assertEquals(REQUEST_PATH, errorResponse.path());
        }
    }

    @Test
    void shouldMapBusinessExceptionWithDefaultStatusCodeToResponse() {
        final String expectedMessage = "Missing mandatory request payload";
        final int expectedDefaultStatus = Response.Status.BAD_REQUEST.getStatusCode(); // 400

        final BusinessException exception = new BusinessException(expectedMessage);

        try (final Response response = mapper.toResponse(exception)) {
            assertEquals(expectedDefaultStatus, response.getStatus());
            assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

            final ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.timestamp());
            assertEquals(expectedDefaultStatus, errorResponse.status());
            assertEquals("Bad Request", errorResponse.error());
            assertEquals(expectedMessage, errorResponse.message());
            assertEquals(REQUEST_PATH, errorResponse.path());
        }
    }

    @Test
    void shouldMapBusinessExceptionWithRootCauseToResponse() {
        final String expectedMessage = "Database constraints violated";
        final Throwable rootCause = new RuntimeException("Underlying database failure");
        final int expectedDefaultStatus = Response.Status.BAD_REQUEST.getStatusCode(); // 400

        final BusinessException exception = new BusinessException(expectedMessage, rootCause);

        try (final Response response = mapper.toResponse(exception)) {
            assertEquals(expectedDefaultStatus, response.getStatus());

            final ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
            assertEquals(expectedDefaultStatus, errorResponse.status());
            assertEquals(expectedMessage, errorResponse.message());
            assertEquals(REQUEST_PATH, errorResponse.path());
        }
    }
}