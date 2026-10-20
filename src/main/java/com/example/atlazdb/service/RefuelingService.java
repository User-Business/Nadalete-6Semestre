package com.example.atlazdb.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.atlazdb.dto.RefuelingRequestDTO;
import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.model.ServiceOrder;
import com.example.atlazdb.repository.CityRepository;
import com.example.atlazdb.repository.RefuelingRepository;
import com.example.atlazdb.repository.ServiceOrderRepository;
import com.example.atlazdb.repository.UserRepository;
import com.example.atlazdb.repository.VehicleRepository;

@Service
public class RefuelingService {

    private final RefuelingRepository repository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final CityRepository cityRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    public RefuelingService(RefuelingRepository repository,
                            UserRepository userRepository,
                            VehicleRepository vehicleRepository,
                            CityRepository cityRepository,
                            ServiceOrderRepository serviceOrderRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.cityRepository = cityRepository;
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public List<Refueling> listAll() {
        return repository.findAll();
    }

    public Refueling findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refueling not found."));
    }

    public Refueling save(RefuelingRequestDTO dto) {
        Refueling refueling = new Refueling();
        apply(refueling, dto);
        return repository.save(refueling);
    }

    public Refueling update(Long id, RefuelingRequestDTO dto) {
        Refueling refueling = findById(id);
        apply(refueling, dto);
        return repository.save(refueling);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    public List<Refueling> searchWithFilters(FuelType fuelType, BigDecimal totalValue, BigDecimal currentKm) {
        if (fuelType != null) {
            return repository.findByFuelType(fuelType);
        }
        if (totalValue != null) {
            return repository.findByTotalValueGreaterThan(totalValue);
        }
        if (currentKm != null) {
            return repository.findByCurrentKmGreaterThan(currentKm);
        }
        return repository.findAll();
    }

    public List<Refueling> findByMonthAndYear(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        return repository.findByPeriod(start, start.plusMonths(1));
    }

    public Long countRefuelingsByDateInterval(LocalDate startDate, LocalDate endDate) {
        return repository.countByDateTimeBetween(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    public List<Refueling> findByInterval(LocalDate startDate, LocalDate endDate) {
        return repository.findByPeriod(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    private void apply(Refueling refueling, RefuelingRequestDTO dto) {
        refueling.setUser(userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
        refueling.setVehicle(vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found.")));
        refueling.setCity(cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found.")));

        // The service order is optional; the current km is only known when the trip is finished
        ServiceOrder serviceOrder = findOptionalServiceOrder(dto.getServiceOrderId());
        refueling.setServiceOrder(serviceOrder);
        refueling.setCurrentKm(serviceOrder != null ? serviceOrder.getArrivalKm() : null);

        refueling.setTotalValue(dto.getTotalValue());
        refueling.setLiters(dto.getLiters());
        refueling.setDateTime(dto.getDateTime());
        refueling.setReceiptNumber(dto.getReceiptNumber());
    }

    private ServiceOrder findOptionalServiceOrder(Long serviceOrderId) {
        if (serviceOrderId == null) {
            return null;
        }
        return serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found."));
    }
}
