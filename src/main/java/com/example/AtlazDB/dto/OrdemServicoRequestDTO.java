package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.TipoOcorrencia;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrdemServicoRequestDTO {

    private TipoOcorrencia tipoServico;
    private String localDestino;
    private String justificativa;
    private String requisitante;
    private BigDecimal kmSaida;
    private BigDecimal kmChegada;
    private LocalDateTime dataSaida;
    private LocalDateTime dataRetorno;
    private Long idUsuario;
    private Long idViatura;


}
