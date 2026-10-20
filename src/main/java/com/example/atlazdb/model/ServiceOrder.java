package com.example.atlazdb.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.atlazdb.enums.OccurrenceType;

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
@Table(name = "ordem_servico")
@Getter
@Setter
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_os")
    private Long id;

    @Column(name = "tipo_servico")
    @Enumerated(EnumType.STRING)
    private OccurrenceType serviceType;

    @Column(name = "local_destino")
    private String destinationLocation;

    @Column(name = "justificativa")
    private String justification;

    @Column(name = "requisitante")
    private String requester;

    @Column(name = "km_saida")
    private BigDecimal departureKm;

    @Column(name = "km_chegada")
    private BigDecimal arrivalKm;

    @Column(name = "data_saida")
    private LocalDateTime departureDate;

    @Column(name = "data_retorno")
    private LocalDateTime returnDate;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_viatura")
    private Vehicle vehicle;
}
