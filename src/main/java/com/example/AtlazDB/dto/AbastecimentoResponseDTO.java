package com.example.AtlazDB.dto;

import com.example.AtlazDB.model.Abastecimento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AbastecimentoResponseDTO(
        Long id,
        LocalDateTime dataHora,
        BigDecimal litros,
        BigDecimal valorTotal,
        String numeroNotaFiscal
) {

    public AbastecimentoResponseDTO(Abastecimento abastecimento) {
        this(
                abastecimento.getId(),
                abastecimento.getDataHora(),
                abastecimento.getLitros(),
                abastecimento.getValorTotal(),
                abastecimento.getNumeroNotaFiscal()
        );
    }
}
