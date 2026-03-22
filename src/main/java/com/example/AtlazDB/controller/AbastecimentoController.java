package com.example.AtlazDB.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.Abastecimento;
import com.example.AtlazDB.service.AbastecimentoService;

@RestController
@RequestMapping("/abastecimentos")
public class AbastecimentoController {

    private final AbastecimentoService service;

    public AbastecimentoController(AbastecimentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Abastecimento> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Abastecimento buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Abastecimento criar(@RequestBody Abastecimento abastecimento) {
        return service.salvar(abastecimento);
    }

    @PutMapping("/{id}")
    public Abastecimento atualizar(@PathVariable Long id, @RequestBody Abastecimento abastecimento) {
        abastecimento.setId(id);
        return service.salvar(abastecimento);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
