package com.example.AtlazDB.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.AtlazDB.enums.*;
import lombok.Data;

@Entity
@Table(name = "abastecimento")
@Data
public class Refueling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abastecimento")
    private Long id;

    @Column(name = "data_hora")
    private LocalDateTime dateTime;

    @Column(name = "km_atual")
    private BigDecimal currentKm;

    @Column(name = "litros")
    private BigDecimal liters;

    @Column(name = "valor_total")
    private BigDecimal totalValue;

    @Column(name = "tipo_combustivel")
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(name = "numero_nota_fiscal")
    private String receiptNumber;

    @Column(name = "observacao_estado")
    private String stateObservation;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_viatura")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "id_cidade")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private City city;

    @ManyToOne
    @JoinColumn(name = "id_os")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ServiceOrder serviceOrder;
}