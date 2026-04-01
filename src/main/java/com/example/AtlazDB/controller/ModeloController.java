package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.ModeloRequestDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.Modelo;
import com.example.AtlazDB.service.ModeloService;

@RestController
@RequestMapping("/modelos")
public class ModeloController {

    private final ModeloService service;

    public ModeloController(ModeloService service) {
        this.service = service;
    }

    @GetMapping
    public List<Modelo> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Modelo buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Modelo criar(@RequestBody ModeloRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    public Modelo atualizar(@PathVariable Long id, @RequestBody ModeloRequestDTO modelo) {
        return service.atualizar(id,modelo);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
