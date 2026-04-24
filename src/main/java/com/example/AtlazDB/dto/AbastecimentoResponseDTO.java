package com.example.AtlazDB.dto;

import com.example.AtlazDB.model.Abastecimento;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AbastecimentoResponseDTO {

    private Long id;
    private LocalDateTime data;
    private BigDecimal valor;
    private String numeroNotaFiscal;

    public static AbastecimentoResponseDTO fromEntity(Abastecimento abast) {
        return new AbastecimentoResponseDTO(
                abast.getId(),
                abast.getDataHora(),
                abast.getValorTotal(),
                abast.getNumeroNotaFiscal()
        );
    }
}