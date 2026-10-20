package com.example.atlazdb.dto;

import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.enums.VehicleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequestDTO {

    @NotBlank
    private String prefix;

    @NotNull
    private VehicleType type;

    @NotNull
    private Long modelId;

    @NotNull
    private FuelType fuelType;

    @PositiveOrZero
    private Double km;

    private VehicleStatus status;
    private CnhType tipoCnhNecessaria;

    @PositiveOrZero
    private Double kmTrocaOleo;
}
