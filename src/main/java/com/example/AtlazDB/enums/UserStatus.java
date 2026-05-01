package com.example.AtlazDB.enums;

public enum UserStatus {
    DISPONIVEL("DISPONIVEL"),
    MANUTENCAO("MANUTENCAO"),
    EM_USO("EM USO"), 
    DESATIVADA("DESATIVADA");


    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
