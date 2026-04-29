package com.example.AtlazDB.model;

import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "matricula")
    private String registration;

    @Column(name = "email")
    private String email;

    @Column(name = "senha_hash")
    private String passwordHash;

    @Column(name = "perfil")
    @Enumerated(EnumType.STRING)
    private Profile profile;

    @Column(name = "usuario_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

}