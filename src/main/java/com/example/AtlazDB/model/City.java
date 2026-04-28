package com.example.AtlazDB.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cidade")
@Data
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cidade")
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "uf")
    private String state;
}