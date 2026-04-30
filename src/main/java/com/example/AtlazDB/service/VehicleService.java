package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.VehicleRequestDTO;
import com.example.AtlazDB.dto.VehicleResponseDTO;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.Model;
import com.example.AtlazDB.model.Vehicle;
import com.example.AtlazDB.repository.ModelRepository;
import com.example.AtlazDB.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public VehicleResponseDTO save(VehicleRequestDTO dto) {

        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new RuntimeException("Model not found!"));

        Vehicle vehicle = new Vehicle();

        vehicle.setPrefix(dto.getPrefix());
        vehicle.setType(dto.getType());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setModel(model);

        vehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        vehicle.setKm(0.0);

        Vehicle saved = repository.save(vehicle);

        return new VehicleResponseDTO(saved);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public VehicleResponseDTO update(Long id, VehicleRequestDTO dto) {

        Vehicle vehicle = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found."));

        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new RuntimeException("Model not found!"));

        vehicle.setPrefix(dto.getPrefix());
        vehicle.setType(dto.getType());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setModel(model);

        Vehicle updated = repository.save(vehicle);

        return new VehicleResponseDTO(updated);
    }
}