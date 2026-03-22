package com.example.AtlazDB.controller;

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
    public Modelo criar(@RequestBody Modelo modelo) {
        return service.salvar(modelo);
    }

    @PutMapping("/{id}")
    public Modelo atualizar(@PathVariable Long id, @RequestBody Modelo modelo) {
        modelo.setId(id);
        return service.salvar(modelo);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
