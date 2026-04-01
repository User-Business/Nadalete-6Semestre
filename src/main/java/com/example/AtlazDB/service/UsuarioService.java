package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.UsuarioRequestDTO;
import com.example.AtlazDB.enums.Perfil;
import com.example.AtlazDB.enums.UsuarioStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.Usuario;
import com.example.AtlazDB.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Usuario salvar(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();

        usuario.setNome(dto.getNome());
        usuario.setMatricula(dto.getMatricula());
        usuario.setSenhaHash(dto.getSenhaHash());
        usuario.setEmail(dto.getEmail());
        usuario.setPerfil((dto.getPerfil()));
        usuario.setUsuarioStatus(UsuarioStatus.ATIVO);
        return repository.save(usuario);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Usuario atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setNome(dto.getNome());
        usuario.setMatricula(dto.getMatricula());
        usuario.setSenhaHash(dto.getSenhaHash());
        usuario.setEmail(dto.getEmail());
        usuario.setPerfil(dto.getPerfil());
        usuario.setUsuarioStatus(UsuarioStatus.ATIVO);
        return repository.save(usuario);
    }
}
