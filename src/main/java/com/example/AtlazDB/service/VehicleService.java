package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.VehicleRequestDTO;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.Model;
import com.example.AtlazDB.repository.ModelRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.Vehicle;
import com.example.AtlazDB.repository.VehicleRepository;

@Service
public class VehicleService {

    private final VehicleRepository repository;
    private final ModelRepository modelRepository;

    public VehicleService(VehicleRepository repository, ModelRepository modelRepository) {
        this.repository = repository;
        this.modelRepository = modelRepository;
    }

    public List<Vehicle> listAll() {
        return repository.findAll();
    }

    public Optional<Vehicle> findById(Long id) {
        return repository.findById(id);
    }

    public Vehicle save(VehicleRequestDTO dto) {
        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new RuntimeException("Model not found!"));

        Vehicle vehicle = new Vehicle();
        vehicle.setType(dto.getType());
        vehicle.setPrefix(dto.getPrefix());
        vehicle.setVehicleStatus(VehicleStatus.ATIVO);
        vehicle.setModel(model);

        return repository.save(vehicle);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Vehicle update(Long id, VehicleRequestDTO dto) {
        Vehicle vehicle = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found."));

        vehicle.setType(dto.getType());
        vehicle.setPrefix(dto.getPrefix());
        vehicle.setVehicleStatus(VehicleStatus.ATIVO);

        return repository.save(vehicle);
    }
}