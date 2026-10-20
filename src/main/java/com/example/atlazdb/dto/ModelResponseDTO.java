package com.example.atlazdb.dto;

import com.example.atlazdb.model.Model;

public record ModelResponseDTO(
        Long id,
        String modelName,
        String brandName
) {
    // Constructor to convert the Entity into DTO
    public ModelResponseDTO(Model model) {
        this(
                model.getId(),
                model.getModelName(),
                model.getBrandName()
        );
    }
}