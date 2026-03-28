package com.example.AtlazDB.model;

import jakarta.persistence.*;
import com.example.AtlazDB.enums.*;
import lombok.Data;

@Entity
@Table(name = "viatura")
@Data
public class Viatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viatura")
    private Long id;

    private String prefixo;

    @Enumerated(EnumType.STRING)
    private TipoViatura tipo;

    @Column(name = "viatura_status")
    @Enumerated(EnumType.STRING)
    private ViaturaStatus viaturaStatus;

    @ManyToOne
    @JoinColumn(name = "id_modelo")
    private Modelo modelo;
}
