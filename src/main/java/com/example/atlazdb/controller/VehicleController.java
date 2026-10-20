package com.example.atlazdb.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.atlazdb.dto.VehicleRequestDTO;
import com.example.atlazdb.dto.VehicleResponseDTO;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.service.VehicleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/available/{userId}")
    public List<VehicleResponseDTO> listAvailableVehicles(@PathVariable Long userId) {
        return service.findAvailableVehiclesByUser(userId);
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> create(@Valid @RequestBody VehicleRequestDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> update(@PathVariable Long id, @Valid @RequestBody VehicleRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vehicle-status")
    public ResponseEntity<VehicleStatus[]> getVehicleStatuses() {
        return ResponseEntity.ok(VehicleStatus.values());
    }

    @GetMapping("/{id}/average-consumption")
    public ResponseEntity<Double> getAverageConsumption(@PathVariable Long id) {
        return ResponseEntity.ok(service.calculateAverageConsumption(id));
    }

    @PatchMapping("/{id}/km-troca-oleo")
    public ResponseEntity<VehicleResponseDTO> updateOilChangeKm(@PathVariable Long id,
                                                                @RequestBody BigDecimal oilChangeKm) {
        return ResponseEntity.ok(service.updateOilChangeKm(id, oilChangeKm));
    }
}
