package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.TipoCombustivelViatura;
import com.example.AtlazDB.enums.TipoViatura;
import com.example.AtlazDB.enums.ViaturaStatus;
import com.example.AtlazDB.model.Viatura;

public record ViaturaResponseDTO(
       Long id,
       String prefixo,
       String modelo,
       ViaturaStatus status,
       TipoCombustivelViatura tipoCombustivel
) {
    // Construtor para converter a Entidade em DTO
    public ViaturaResponseDTO(Viatura viatura) {
        this(
               viatura.getId(),
                viatura.getPrefixo(),
                viatura.getModelo().getNomeModelo() + " - " + viatura.getModelo().getNomeMarca(),
                viatura.getViaturaStatus(),
                viatura.getTipoCombustivel()
        );
    }
}