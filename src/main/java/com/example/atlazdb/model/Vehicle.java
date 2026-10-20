package com.example.atlazdb.model;

import java.math.BigDecimal;

import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.enums.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "viatura")
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viatura")
    private Long id;

    @Column(name = "prefixo")
    private String prefix;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Column(name = "viatura_status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus vehicleStatus;

    @ManyToOne
    @JoinColumn(name = "id_modelo")
    private Model model;

    @Column(name = "tipo_abastecimento", nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(name = "km_atual")
    private Double km;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cnh_necessaria")
    private CnhType tipoCnhNecessaria;

    @Column(name = "km_troca_oleo")
    private BigDecimal kmTrocaOleo;
}
