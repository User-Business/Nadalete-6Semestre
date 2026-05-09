package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.VehicleRequestDTO;
import com.example.AtlazDB.dto.VehicleResponseDTO;
import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.Model;
import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.model.User;
import com.example.AtlazDB.repository.UserRepository;
import com.example.AtlazDB.model.Vehicle;
import com.example.AtlazDB.repository.ModelRepository;
import com.example.AtlazDB.repository.ServiceOrderRepository;
import com.example.AtlazDB.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository repository;
    private final ModelRepository modelRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository repository, ModelRepository modelRepository, ServiceOrderRepository serviceOrderRepository, UserRepository userRepository) {
        this.repository = repository;
        this.modelRepository = modelRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.userRepository = userRepository;
    }

    public List<VehicleResponseDTO> findAvailableVehiclesByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return repository.findAll()
                .stream()

                // somente viaturas disponiveis
                .filter(vehicle ->
                        vehicle.getVehicleStatus() == VehicleStatus.DISPONIVEL
                )

                // somente viaturas compativeis com a CNH
                .filter(vehicle ->
                        user.getTiposCnh()
                                .contains(vehicle.getTipoCnhNecessaria())
                )

                .map(VehicleResponseDTO::new)
                .toList();
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
        vehicle.setTipoCnhNecessaria(
                dto.getTipoCnhNecessaria() != null
                ? dto.getTipoCnhNecessaria()
                : CnhType.B);
        vehicle.setModel(model);

        vehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        vehicle.setKm(dto.getKm() != null ? dto.getKm() : 0.0);

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
        vehicle.setTipoCnhNecessaria(dto.getTipoCnhNecessaria());
        vehicle.setModel(model);

        if (dto.getStatus() != null) {
            vehicle.setVehicleStatus(dto.getStatus());
        }
        if (dto.getKm() != null) {
            vehicle.setKm(dto.getKm());
        }
        if (dto.getTipoCnhNecessaria() != null) {
            vehicle.setTipoCnhNecessaria(dto.getTipoCnhNecessaria());
        }

        Vehicle updated = repository.save(vehicle);

        return new VehicleResponseDTO(updated);
    }
    
    public void updateCurrentKm(Long vehicleId) {
    ServiceOrder last = serviceOrderRepository
            .findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(vehicleId);

    if (last != null && last.getArrivalKm() != null) {
        Vehicle vehicle = repository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        vehicle.setKm(last.getArrivalKm().doubleValue());
        repository.save(vehicle);
    }
}
}