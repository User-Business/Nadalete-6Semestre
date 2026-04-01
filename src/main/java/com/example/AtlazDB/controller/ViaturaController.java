package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.ViaturaRequestDTO;
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
    public Viatura criar(@RequestBody ViaturaRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    public Viatura atualizar(@PathVariable Long id, @RequestBody ViaturaRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
