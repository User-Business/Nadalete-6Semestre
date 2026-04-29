package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.VehicleType;


public class VehicleRequestDTO {

    private String prefix;
    private VehicleType type;
    private Long modelId;
    
    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public VehicleType getType() {
        return type;
    }
    public void setType(VehicleType type) {
        this.type = type;
    }
    public Long getModelId() {
        return modelId;
    }
    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }
}
