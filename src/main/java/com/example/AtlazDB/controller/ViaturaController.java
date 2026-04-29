package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.ViaturaRequestDTO;
import com.example.AtlazDB.dto.ViaturaResponseDTO;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ViaturaResponseDTO>> listar() {
      
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViaturaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ViaturaResponseDTO> criar(@RequestBody ViaturaRequestDTO dto) {
        Viatura salvo = service.salvar(dto);
        return ResponseEntity.ok(new ViaturaResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViaturaResponseDTO> atualizar(@PathVariable Long id, @RequestBody ViaturaRequestDTO dto) {
        Viatura atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(new ViaturaResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
