package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.ServiceOrderRequestDTO;
import com.example.AtlazDB.enums.OccurrenceType;
import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.repository.RefuelingRepository;
import com.example.AtlazDB.service.RefuelingService;
import com.example.AtlazDB.service.GenerateCsv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.service.ServiceOrderService;

@RestController
@RequestMapping("/service-orders")
public class ServiceOrderController {

    private final ServiceOrderService service;
    private final RefuelingService refuelingService;
    private final GenerateCsv generateCsv;

    public ServiceOrderController(ServiceOrderService service,
                                  RefuelingService refuelingService,
                                  GenerateCsv generateCsv) {
        this.service = service;
        this.refuelingService = refuelingService;
        this.generateCsv = generateCsv;
    }

    @GetMapping
    public List<ServiceOrder> list() {
        return service.listAll();
    }

    @GetMapping("/occurrence-types")
    public OccurrenceType[] listTypes() {
        return OccurrenceType.values();
    }

    @GetMapping("/{id}")
    public ServiceOrder findById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public ServiceOrder create(@RequestBody ServiceOrderRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public ServiceOrder update(@PathVariable Long id, @RequestBody ServiceOrderRequestDTO dto) {
        return service.update(id,dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Autowired
    private RefuelingRepository refuelingRepository;

    @GetMapping("/csv")
    public ResponseEntity<byte[]> downloadCSV(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        List<ServiceOrder> orders = (month != null && year != null)
                ? service.findByMonthAndYear(month, year)
                : service.listAll();

        List<Refueling> refuelings = (month != null && year != null)
                ? refuelingService.findByMonthAndYear(month, year)
                : refuelingService.listAll();

        byte[] csvBytes = generateCsv.generateCSV(orders, refuelings);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=records.csv")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .body(csvBytes);
    }

    @GetMapping("/by-month")
    public ResponseEntity<List<ServiceOrder>> findByMonth(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(service.findByMonthAndYear(month, year));
    }
}