package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.FuelType;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.enums.VehicleType;
import com.example.AtlazDB.model.Vehicle;

public record VehicleResponseDTO(
        Long id,
        String prefix,
        String model,
        String brand,
        VehicleStatus status,
        FuelType fuelType,
        VehicleType type,
        Double km
) {
    public VehicleResponseDTO(Vehicle vehicle) {
        this(
                vehicle.getId(),
                vehicle.getPrefix(),
                vehicle.getModel().getModelName(),
                vehicle.getModel().getBrandName(),
                vehicle.getVehicleStatus(),
                vehicle.getFuelType(),
                vehicle.getType(),
                vehicle.getKm()
        );
    }
}