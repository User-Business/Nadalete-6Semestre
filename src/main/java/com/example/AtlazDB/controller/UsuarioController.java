package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.UsuarioRequestDTO;
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
    public List<Usuario> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Usuario criar(@RequestBody UsuarioRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
