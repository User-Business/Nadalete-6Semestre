package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.CidadeRequestDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.Cidade;
import com.example.AtlazDB.service.CidadeService;

@RestController
@RequestMapping("/cidades")
public class CidadeController {

    private final CidadeService service;

    public CidadeController(CidadeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cidade> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Cidade buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Cidade criar(@RequestBody CidadeRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    public Cidade atualizar(@PathVariable Long id, @RequestBody CidadeRequestDTO dto) {
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
