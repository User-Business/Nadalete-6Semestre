package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.TipoOcorrencia;
import com.example.AtlazDB.model.OrdemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdemServicoResponseDTO(
        Long id,
        TipoOcorrencia tipoServico,
        String localDestino,
        String justificativa,
        String requisitante,
        BigDecimal kmSaida,
        BigDecimal kmChegada,
        LocalDateTime dataSaida,
        LocalDateTime dataRetorno
) {
    // Construtor para converter a Entidade em DTO
    public OrdemServicoResponseDTO(OrdemServico ordemServico) {
        this(
                ordemServico.getId(),
                ordemServico.getTipoServico(),
                ordemServico.getLocalDestino(),
                ordemServico.getJustificativa(),
                ordemServico.getRequisitante(),
                ordemServico.getKmSaida(),
                ordemServico.getKmChegada(),
                ordemServico.getDataSaida(),
                ordemServico.getDataRetorno()
        );
    }
}