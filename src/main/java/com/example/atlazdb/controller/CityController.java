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

import com.example.atlazdb.dto.CityRequestDTO;
import com.example.atlazdb.model.City;
import com.example.atlazdb.service.CityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cities")
public class CityController {

    private final CityService service;

    public CityController(CityService service) {
        this.service = service;
    }

    @GetMapping
    public List<City> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public City findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public City create(@Valid @RequestBody CityRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public City update(@PathVariable Long id, @Valid @RequestBody CityRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
