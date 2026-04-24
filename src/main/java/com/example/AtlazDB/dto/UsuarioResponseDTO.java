package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.Perfil;
import com.example.AtlazDB.model.Usuario;


import com.example.AtlazDB.enums.UsuarioStatus; 

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String matricula,
        String email,
        Perfil perfil,
        UsuarioStatus usuarioStatus 
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getMatricula(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getUsuarioStatus()
        );
    }
}