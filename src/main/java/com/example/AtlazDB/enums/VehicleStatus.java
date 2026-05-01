package com.example.AtlazDB.enums;

public enum VehicleStatus {
    DISPONIVEL("DISPONIVEL"),
    MANUTENCAO("MANUTENCAO"),
    EM_USO("EM USO"), 
    DESATIVADA("DESATIVADA");


    private final String value;

    VehicleStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}