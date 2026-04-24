package com.example.AtlazDB.dto;

import com.example.AtlazDB.model.Cidade;

public record CidadeResponseDTO(
        Long id,
        String nome,
        String uf
) {
    // Construtor para converter a Entidade em DTO
    public CidadeResponseDTO(Cidade cidade) {
        this(
                cidade.getId(),
                cidade.getNome(),
                cidade.getUf()
        );
    }
}