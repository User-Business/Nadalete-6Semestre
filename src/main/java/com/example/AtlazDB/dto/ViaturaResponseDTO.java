package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.TipoViatura;
import com.example.AtlazDB.model.Viatura;

public record ViaturaResponseDTO(
        Long id,
        String prefixo,
        TipoViatura tipo,
        Long idModelo
) {
    // Construtor para converter a Entidade em DTO
    public ViaturaResponseDTO(Viatura viatura) {
        this(
                viatura.getId(),
                viatura.getPrefixo(),
                viatura.getTipo(),
                viatura.getModelo().getId()
        );
    }
}