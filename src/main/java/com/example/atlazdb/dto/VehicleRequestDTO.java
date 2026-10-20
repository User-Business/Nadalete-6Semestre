package com.example.atlazdb.dto;

import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.enums.VehicleType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequestDTO {

    private String prefix;
    private VehicleType type;
    private Long modelId;
    private FuelType fuelType;
    private Double km;
    private VehicleStatus status;
    private CnhType tipoCnhNecessaria;
    private Double kmTrocaOleo;
    

}