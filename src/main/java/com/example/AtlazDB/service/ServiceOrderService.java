package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.ServiceOrderRequestDTO;
import com.example.AtlazDB.model.User;
import com.example.AtlazDB.model.Vehicle;
import com.example.AtlazDB.repository.UserRepository;
import com.example.AtlazDB.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.repository.ServiceOrderRepository;

@Service
public class ServiceOrderService {

    private final ServiceOrderRepository repository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public ServiceOrderService(ServiceOrderRepository repository,
                               UserRepository userRepository,
                               VehicleRepository vehicleRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<ServiceOrder> listAll() {
        return repository.findAll();
    }

    public Optional<ServiceOrder> findById(Long id) {
        return repository.findById(id);
    }

    public List<ServiceOrder> findByMonthAndYear(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return repository.findByPeriod(start, end);
    }

    public ServiceOrder save(ServiceOrderRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found!"));

        ServiceOrder so = new ServiceOrder();
        so.setServiceType(dto.getServiceType());
        so.setDestinationLocation(dto.getDestinationLocation());
        so.setJustification(dto.getJustification());
        so.setRequester(dto.getRequester());
        so.setDepartureKm(dto.getDepartureKm());
        so.setArrivalKm(dto.getArrivalKm());
        so.setDepartureDate(dto.getDepartureDate());
        so.setReturnDate(dto.getReturnDate());
        so.setUser(user);
        so.setVehicle(vehicle);

        return repository.save(so);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public ServiceOrder update(Long id, ServiceOrderRequestDTO dto) {
        ServiceOrder serviceOrder = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service order not found!"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found!"));

        serviceOrder.setServiceType(dto.getServiceType());
        serviceOrder.setDestinationLocation(dto.getDestinationLocation());
        serviceOrder.setJustification(dto.getJustification());
        serviceOrder.setRequester(dto.getRequester());
        serviceOrder.setDepartureKm(dto.getDepartureKm());
        serviceOrder.setArrivalKm(dto.getArrivalKm());
        serviceOrder.setDepartureDate(dto.getDepartureDate());
        serviceOrder.setReturnDate(dto.getReturnDate());
        serviceOrder.setUser(user);
        serviceOrder.setVehicle(vehicle);

        return repository.save(serviceOrder);
    }
}