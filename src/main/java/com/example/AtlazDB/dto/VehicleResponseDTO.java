package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.VehicleType;
import com.example.AtlazDB.model.Vehicle;

public record VehicleResponseDTO(
        Long id,
        String prefix,
        VehicleType type,
        Long modelId
) {
    // Constructor to convert the Entity into DTO
    public VehicleResponseDTO(Vehicle vehicle) {
        this(
                vehicle.getId(),
                vehicle.getPrefix(),
                vehicle.getType(),
                vehicle.getModel().getId()
        );
    }
}