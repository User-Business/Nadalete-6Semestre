package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.OrdemServicoRequestDTO;
import com.example.AtlazDB.enums.TipoOcorrencia;
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

    @GetMapping("/tipos-ocorrencia")
    public TipoOcorrencia[] listarTipos() {
        return TipoOcorrencia.values();
    }

    @GetMapping("/{id}")
    public OrdemServico buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public OrdemServico criar(@RequestBody OrdemServicoRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    public OrdemServico atualizar(@PathVariable Long id, @RequestBody OrdemServicoRequestDTO dto) {
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
