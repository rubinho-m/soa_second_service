package com.rubinho.soa_second_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllVehiclePageableResponse {
    private List<Vehicle> vehicles;

    private int totalPages;

    private int page;

    private int pageSize;
}
