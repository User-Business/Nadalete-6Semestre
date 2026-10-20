package com.example.atlazdb.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.atlazdb.dto.RefuelingRequestDTO;
import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.service.RefuelingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/refuelings")
public class RefuelingController {

    private final RefuelingService service;

    public RefuelingController(RefuelingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Refueling> search(
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) BigDecimal totalValue,
            @RequestParam(required = false) BigDecimal currentKm) {
        return service.searchWithFilters(fuelType, totalValue, currentKm);
    }

    @GetMapping("/{id}")
    public Refueling findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Refueling create(@Valid @RequestBody RefuelingRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public Refueling update(@PathVariable Long id, @Valid @RequestBody RefuelingRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/by-month")
    public ResponseEntity<List<Refueling>> findByMonth(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(service.findByMonthAndYear(month, year));
    }

    @GetMapping("/count-by-date")
    public ResponseEntity<Long> countRefuelingsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(service.countRefuelingsByDateInterval(startDate, endDate));
    }
}
