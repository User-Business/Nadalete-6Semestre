package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.Perfil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequestDTO {
    private String nome;
    private String matricula;
    private String email;
    private String senhaHash;
    private Perfil perfil;
    private String usuarioStatus;
}
