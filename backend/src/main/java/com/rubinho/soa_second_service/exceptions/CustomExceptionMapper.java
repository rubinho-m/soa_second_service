package com.rubinho.soa_second_service.exceptions;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        System.out.println("exception" + exception);
        if (exception instanceof ServiceUnavailableException) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ErrorMessage("Vehicle service is temporarily unavailable"))
                    .build();
        }
        if (exception instanceof BadRequestException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Invalid path param"))
                    .build();
        }
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Not found"))
                    .build();
        }
        if (exception.getClass().getSimpleName().contains("PathParamException")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Invalid path param"))
                    .build();
        }
        final Response response = exception.getResponse();
        if (response != null) {
            final int statusCode = response.getStatus();
            if (statusCode == 503) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ErrorMessage("Vehicle service is temporarily unavailable"))
                        .build();
            }
            return Response.status(statusCode)
                    .entity(new ErrorMessage(exception.getMessage()))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorMessage(exception.getMessage()))
                .build();
    }
}