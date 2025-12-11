package com.rubinho.soa_second_service.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        if (exception.getClass().getSimpleName().contains("PathParamException")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid path param")
                    .build();
        }
        final int statusCode = exception.getResponse().getStatus();
        if (statusCode == 503) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Vehicle service is temporarily unavailable")
                    .build();
        }
        return Response.status(statusCode)
                .entity(exception.getMessage())
                .build();
    }
}