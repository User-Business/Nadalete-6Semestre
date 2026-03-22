package com.example.AtlazDB.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.Viatura;
import com.example.AtlazDB.service.ViaturaService;

@RestController
@RequestMapping("/viaturas")
public class ViaturaController {

    private final ViaturaService service;

    public ViaturaController(ViaturaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Viatura> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Viatura buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Viatura criar(@RequestBody Viatura viatura) {
        return service.salvar(viatura);
    }

    @PutMapping("/{id}")
    public Viatura atualizar(@PathVariable Long id, @RequestBody Viatura viatura) {
        viatura.setId(id);
        return service.salvar(viatura);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
