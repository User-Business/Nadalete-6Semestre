package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.VehicleRequestDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.Vehicle;
import com.example.AtlazDB.service.VehicleService;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping
    public List<Vehicle> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Vehicle findById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public Vehicle create(@RequestBody VehicleRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public Vehicle update(@PathVariable Long id, @RequestBody VehicleRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}