package com.rubinho.soa_second_service.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        responseContext.getHeaders().add("Access-Control-Allow-Origin", "https://se.ifmo.ru");

        responseContext.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, PATCH, OPTIONS");

        responseContext.getHeaders().add("Access-Control-Allow-Headers",
                "Origin, Content-Type, Accept, Authorization, X-Requested-With");

        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");

        responseContext.getHeaders().add("Access-Control-Expose-Headers",
                "Content-Length, Content-Type, Location");
    }
}
