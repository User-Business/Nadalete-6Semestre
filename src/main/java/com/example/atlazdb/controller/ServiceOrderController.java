package com.example.atlazdb.controller;

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

import com.example.atlazdb.dto.ServiceOrderRequestDTO;
import com.example.atlazdb.enums.OccurrenceType;
import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.model.ServiceOrder;
import com.example.atlazdb.service.GenerateCsv;
import com.example.atlazdb.service.RefuelingService;
import com.example.atlazdb.service.ServiceOrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/service-orders")
public class ServiceOrderController {

    private static final String CATEGORY_REFUELING = "abastecimento";
    private static final String CATEGORY_SERVICE_ORDER = "os";

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
        return service.findById(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ServiceOrder>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    @PostMapping
    public ServiceOrder create(@Valid @RequestBody ServiceOrderRequestDTO dto) {
        return service.save(dto);
    }

    // Only the arrival fields are used when finishing a trip, so the full payload is not required here
    @PutMapping("/{id}")
    public ServiceOrder update(@PathVariable Long id, @RequestBody ServiceOrderRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> downloadCSV(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String categoria) {

        List<ServiceOrder> orders;
        List<Refueling> refuelings;

        if (dataInicio != null && dataFim != null) {
            orders = service.findByInterval(dataInicio, dataFim);
            refuelings = refuelingService.findByInterval(dataInicio, dataFim);
        } else if (month != null && year != null) {
            orders = service.findByMonthAndYear(month, year);
            refuelings = refuelingService.findByMonthAndYear(month, year);
        } else {
            orders = service.listAll();
            refuelings = refuelingService.listAll();
        }

        if (CATEGORY_REFUELING.equals(categoria)) {
            orders = List.of();
        } else if (CATEGORY_SERVICE_ORDER.equals(categoria)) {
            refuelings = List.of();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=records.csv")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .body(generateCsv.generateCSV(orders, refuelings));
    }

    @GetMapping("/by-month")
    public ResponseEntity<List<ServiceOrder>> findByMonth(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(service.findByMonthAndYear(month, year));
    }

    @GetMapping("/count-by-interval")
    public ResponseEntity<Long> countByInterval(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(service.countServiceOrdersByDateInterval(dataInicio, dataFim));
    }
}
