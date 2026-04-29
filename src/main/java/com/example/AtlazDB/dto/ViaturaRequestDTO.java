package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.TipoViatura;
import com.example.AtlazDB.enums.TipoCombustivelViatura;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViaturaRequestDTO {

    private String prefixo;
    private TipoViatura tipo;
    private Long idModelo;
    private TipoCombustivelViatura tipoCombustivel;
}