package com.example.AtlazDB.model;

import com.example.AtlazDB.enums.Perfil;
import com.example.AtlazDB.enums.UsuarioStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    private String nome;

    private String matricula;

    private String email;

    @Column(name = "senha_hash")
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    @Column(name = "usuario_status")
    @Enumerated(EnumType.STRING)
    private UsuarioStatus usuarioStatus;

 }
