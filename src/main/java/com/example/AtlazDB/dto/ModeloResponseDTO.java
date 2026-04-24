package com.example.AtlazDB.dto;

import com.example.AtlazDB.model.Modelo;

public record ModeloResponseDTO(
        Long id,
        String nomeModelo,
        String nomeMarca
) {
    // Construtor para converter a Entidade em DTO
    public ModeloResponseDTO(Modelo modelo) {
        this(
                modelo.getId(),
                modelo.getNomeModelo(),
                modelo.getNomeMarca()
        );
    }
}