package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.ModeloRequestDTO;
import com.example.AtlazDB.dto.ModeloResponseDTO;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ModeloResponseDTO>> listar() {
        List<Modelo> lista = service.listarTodos();
        
        List<ModeloResponseDTO> dtoLista = lista.stream()
                .map(ModeloResponseDTO::new)
                .toList();
                
        return ResponseEntity.ok(dtoLista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModeloResponseDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ModeloResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ModeloResponseDTO> criar(@RequestBody ModeloRequestDTO dto) {
        Modelo salvo = service.salvar(dto);
        return ResponseEntity.ok(new ModeloResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModeloResponseDTO> atualizar(@PathVariable Long id, @RequestBody ModeloRequestDTO dto) {
        Modelo atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(new ModeloResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
