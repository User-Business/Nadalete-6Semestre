package com.example.AtlazDB.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.OrdemServico;
import com.example.AtlazDB.service.OrdemServicoService;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrdemServico> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public OrdemServico buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public OrdemServico criar(@RequestBody OrdemServico os) {
        return service.salvar(os);
    }

    @PutMapping("/{id}")
    public OrdemServico atualizar(@PathVariable Long id, @RequestBody OrdemServico os) {
        os.setId(id);
        return service.salvar(os);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
