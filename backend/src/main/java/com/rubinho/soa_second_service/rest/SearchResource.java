package com.rubinho.soa_second_service.rest;

import com.rubinho.ejb.service.VehicleService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchResource {
    @EJB
    private VehicleService vehicleService;

    @GET
    @Path("/by-number-of-wheels/{from}/{to}")
    public Response byNumberOfWheels(@PathParam("from") Float from, @PathParam("to") Float to) {
        return Response.ok(
               vehicleService.getAllVehiclesByNumberOfWheels(from, to)
        ).build();
    }

    @GET
    @Path("/by-engine-power/{from}/{to}")
    public Response byEnginePower(@PathParam("from") Float from, @PathParam("to") Float to) {
        return Response.ok(
                vehicleService.getAllVehiclesByEnginePower(from, to)
        ).build();
    }
}