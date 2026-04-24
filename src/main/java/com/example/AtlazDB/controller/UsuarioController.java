package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.UsuarioRequestDTO;
import com.example.AtlazDB.dto.UsuarioResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.Usuario;
import com.example.AtlazDB.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        List<Usuario> lista = service.listarTodos();
        
        List<UsuarioResponseDTO> dtoLista = lista.stream()
                .map(UsuarioResponseDTO::new)
                .toList();
                
        return ResponseEntity.ok(dtoLista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(UsuarioResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody UsuarioRequestDTO dto) {
        Usuario salvo = service.salvar(dto);
        return ResponseEntity.ok(new UsuarioResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        Usuario atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(new UsuarioResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
