package com.example.AtlazDB.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "modelo")
@Data
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modelo")
    private Long id;

    @Column(name = "nome_modelo")
    private String nomeModelo;

    @Column(name = "nome_marca")
    private String nomeMarca;

}
