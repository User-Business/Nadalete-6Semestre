package com.example.atlazdb.model;

import java.util.Set;

import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.Profile;
import com.example.atlazdb.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
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

    // Never expose the password when the entity is serialized (e.g. nested in service orders)
    @JsonIgnore
    @Column(name = "senha_hash")
    private String passwordHash;

    @Column(name = "perfil")
    @Enumerated(EnumType.STRING)
    private Profile profile;

    @Column(name = "usuario_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @ElementCollection(targetClass = CnhType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "usuario_cnh",
            joinColumns = @JoinColumn(name = "id_usuario")
    )
    @Column(name = "tipo_cnh")
    private Set<CnhType> cnhTypes;
}
