package com.example.AtlazDB.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityRequestDTO {
    private String name;
    private String uf;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
