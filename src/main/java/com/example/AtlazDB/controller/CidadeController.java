package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.CidadeRequestDTO;
import com.example.AtlazDB.dto.CidadeResponseDTO;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<CidadeResponseDTO>> listar() {
        List<Cidade> lista = service.listarTodos();
        
        List<CidadeResponseDTO> dtoLista = lista.stream()
                .map(CidadeResponseDTO::new)
                .toList();
                
        return ResponseEntity.ok(dtoLista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CidadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(CidadeResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CidadeResponseDTO> criar(@RequestBody CidadeRequestDTO dto) {
        Cidade salvo = service.salvar(dto);
        return ResponseEntity.ok(new CidadeResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CidadeResponseDTO> atualizar(@PathVariable Long id, @RequestBody CidadeRequestDTO dto) {
        Cidade atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(new CidadeResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
