package com.example.AtlazDB.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.AtlazDB.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "abastecimento")
@Data
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abastecimento")
    private Long id;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "km_atual")
    private BigDecimal kmAtual;

    private BigDecimal litros;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "tipo_combustivel")
    @Enumerated(EnumType.STRING)
    private TipoCombustivel tipoCombustivel;

    @Column(name = "numero_nota_fiscal")
    private String numeroNotaFiscal;

    @Column(name = "observacao_estado")
    private String observacaoEstado;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_viatura")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Viatura viatura;

    @ManyToOne
    @JoinColumn(name = "id_cidade")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cidade cidade;

    @ManyToOne
    @JoinColumn(name = "id_os")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private OrdemServico ordemServico;
}