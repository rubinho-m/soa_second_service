package com.rubinho.soa_second_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    private Integer id;

    private String name;

    private Coordinates coordinates;

    private LocalDate creationDate;

    private Float enginePower;

    private VehicleType type;

    private FuelType fuelType;
}
