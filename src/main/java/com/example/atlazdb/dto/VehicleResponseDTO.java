package com.example.atlazdb.dto;

import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.enums.VehicleType;
import com.example.atlazdb.model.Vehicle;
import com.example.atlazdb.enums.CnhType;

import java.math.BigDecimal;

public record VehicleResponseDTO(
        Long id,
        String prefix,
        String model,
        String brand,
        VehicleStatus status,
        FuelType fuelType,
        VehicleType type,
        Double km,
        Long modelId,
        CnhType tipoCnhNecessaria,
        BigDecimal kmTrocaOleo
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
                vehicle.getKm(),
                vehicle.getModel().getId(),
                vehicle.getTipoCnhNecessaria(),
                vehicle.getKmTrocaOleo()
        );
    }
}