package com.example.AtlazDB.dto;

import java.math.BigDecimal;
import java.util.List;


public record DashboardResponseDTO(DataContainer data) {
    public record DataContainer(Metricas metrics, List<Atividade> ativadesHoje){}

    public record Metricas(
        Integer viaturasAtivas,
        Integer viaturasTotal,
        Integer tecnicosEmCampo,
        BigDecimal consumoMedio  
    ){}

    public record Atividade(
        Long id,
        String tipo,
        String prefixoViatura,
        String nomeTecnico,
        String descricao,
        String status,
        String corStatus
    ){}
}
