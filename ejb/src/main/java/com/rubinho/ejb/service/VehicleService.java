package com.rubinho.ejb.service;

import jakarta.ejb.Remote;

@Remote
public interface VehicleService {
    String getAllVehiclesByNumberOfWheels(float from, float to);

    String getAllVehiclesByEnginePower(float from, float to);
}
