package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.RefuelingRequestDTO;
import com.example.AtlazDB.enums.FuelType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.AtlazDB.service.GenerateCsv;
import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.service.RefuelingService;

@RestController
@RequestMapping("/refuelings")
public class RefuelingController {

    private final RefuelingService service;
//    private final GenerateCsv generateCsv;

    public RefuelingController(RefuelingService service, GenerateCsv generateCsv) {
        this.service = service;
//        this.generateCsv = generateCsv;
    }

    @GetMapping
    public List<Refueling> search(
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) Double totalValue,
            @RequestParam(required = false) Double currentKm
    ) {
        return service.searchWithFilters(fuelType, totalValue, currentKm);
    }


//    @GetMapping("/csv")
//    public ResponseEntity<byte[]> downloadCSV() {
//
//        byte[] csvBytes = generateCsv.generateCSV();
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=refueling.csv")
//                .header("Content-Type", "text/csv")
//                .body(csvBytes);
//    }

    @GetMapping("/{id}")
    public Refueling findById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public Refueling create(@RequestBody RefuelingRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public Refueling update(@PathVariable Long id, @RequestBody RefuelingRequestDTO dto) {
        return service.update(id,dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/by-month")
    public ResponseEntity<List<Refueling>> findByMonth(
            @RequestParam int month,
            @RequestParam int year) {

        List<Refueling> list = service.findByMonthAndYear(month, year);
        return ResponseEntity.ok(list);
    }

}