package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.VehicleRequestDTO;
import com.example.AtlazDB.dto.VehicleResponseDTO;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.Model;
import com.example.AtlazDB.repository.ModelRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public List<VehicleResponseDTO> listAll() {
        return repository.findAll()
                .stream()
                .map(VehicleResponseDTO::new)
                .toList();
    }

    public VehicleResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(VehicleResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicle save(VehicleRequestDTO dto) {
        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new RuntimeException("Model not found!"));

        Vehicle vehicle = new Vehicle();
        vehicle.setType(dto.getType());
        vehicle.setPrefix(dto.getPrefix());
        vehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        vehicle.setModel(model);
        vehicle.setFuelType(dto.getFuelType());

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
        vehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        vehicle.setFuelType(dto.getFuelType());

        return repository.save(vehicle);
    }
}