package com.example.AtlazDB.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelRequestDTO {
    private String nameModel;
    private String nameBrand;

    public String getNameModel() {
        return nameModel;
    }

    public void setNameModel(String nameModel) {
        this.nameModel = nameModel;
    }

    public String getNameBrand() {
        return nameBrand;
    }

    public void setNameBrand(String nameBrand) {
        this.nameBrand = nameBrand;
    }
}
