package com.example.AtlazDB.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_servico")
@Data
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_os")
    private Long id;

    @Column(name = "numero_os")
    private String numeroOs;

    @Column(name = "tipo_servico")
    private String tipoServico;

    @Column(name = "local_destino")
    private String localDestino;

    @Column(name = "km_saida")
    private BigDecimal kmSaida;

    @Column(name = "km_chegada")
    private BigDecimal kmChegada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "data_retorno")
    private LocalDateTime dataRetorno;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_viatura")
    private Viatura viatura;

}
