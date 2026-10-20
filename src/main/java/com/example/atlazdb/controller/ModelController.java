package com.example.atlazdb.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.atlazdb.dto.ModelRequestDTO;
import com.example.atlazdb.model.Model;
import com.example.atlazdb.service.ModelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/models")
public class ModelController {

    private final ModelService service;

    public ModelController(ModelService service) {
        this.service = service;
    }

    @GetMapping
    public List<Model> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Model findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Model create(@Valid @RequestBody ModelRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public Model update(@PathVariable Long id, @Valid @RequestBody ModelRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
