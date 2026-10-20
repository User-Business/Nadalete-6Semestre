package com.example.atlazdb.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.atlazdb.dto.VehicleRequestDTO;
import com.example.atlazdb.dto.VehicleResponseDTO;
import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.exception.BusinessRuleException;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.Model;
import com.example.atlazdb.model.ServiceOrder;
import com.example.atlazdb.model.User;
import com.example.atlazdb.model.Vehicle;
import com.example.atlazdb.repository.ModelRepository;
import com.example.atlazdb.repository.RefuelingRepository;
import com.example.atlazdb.repository.ServiceOrderRepository;
import com.example.atlazdb.repository.UserRepository;
import com.example.atlazdb.repository.VehicleRepository;

@Service
public class VehicleService {

    private static final double DEFAULT_OIL_CHANGE_INTERVAL_KM = 10000;

    private final VehicleRepository repository;
    private final ModelRepository modelRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final UserRepository userRepository;
    private final RefuelingRepository refuelingRepository;
    private final ConsumptionCalculator consumptionCalculator;

    public VehicleService(VehicleRepository repository,
                          ModelRepository modelRepository,
                          ServiceOrderRepository serviceOrderRepository,
                          UserRepository userRepository,
                          RefuelingRepository refuelingRepository,
                          ConsumptionCalculator consumptionCalculator) {
        this.repository = repository;
        this.modelRepository = modelRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.userRepository = userRepository;
        this.refuelingRepository = refuelingRepository;
        this.consumptionCalculator = consumptionCalculator;
    }

    /**
     * Lists the available vehicles that the user is allowed to drive with his driver's licenses.
     */
    public List<VehicleResponseDTO> findAvailableVehiclesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Set<CnhType> licenses = user.getCnhTypes() != null ? user.getCnhTypes() : Set.of();

        return repository.findAll().stream()
                .filter(vehicle -> vehicle.getVehicleStatus() == VehicleStatus.DISPONIVEL)
                .filter(vehicle -> licenses.contains(vehicle.getTipoCnhNecessaria()))
                .map(VehicleResponseDTO::new)
                .toList();
    }

    public List<VehicleResponseDTO> listAll() {
        return repository.findAll().stream()
                .map(VehicleResponseDTO::new)
                .toList();
    }

    public VehicleResponseDTO findById(Long id) {
        return new VehicleResponseDTO(findVehicle(id));
    }

    public VehicleResponseDTO save(VehicleRequestDTO dto) {
        Model model = findModel(dto.getModelId());

        Vehicle vehicle = new Vehicle();
        vehicle.setPrefix(dto.getPrefix());
        vehicle.setType(dto.getType());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setModel(model);
        vehicle.setTipoCnhNecessaria(dto.getTipoCnhNecessaria() != null ? dto.getTipoCnhNecessaria() : CnhType.B);
        vehicle.setVehicleStatus(dto.getStatus() != null ? dto.getStatus() : VehicleStatus.DISPONIVEL);

        double km = dto.getKm() != null ? dto.getKm() : 0.0;
        vehicle.setKm(km);
        // When not informed, the next oil change is scheduled for the current km + default interval
        vehicle.setKmTrocaOleo(BigDecimal.valueOf(
                dto.getKmTrocaOleo() != null ? dto.getKmTrocaOleo() : km + DEFAULT_OIL_CHANGE_INTERVAL_KM));

        return new VehicleResponseDTO(repository.save(vehicle));
    }

    public VehicleResponseDTO update(Long id, VehicleRequestDTO dto) {
        Vehicle vehicle = findVehicle(id);
        Model model = findModel(dto.getModelId());

        if (dto.getStatus() == VehicleStatus.DESATIVADA
                && serviceOrderRepository.existsByVehicleIdAndReturnDateIsNull(id)) {
            throw new BusinessRuleException(
                    "Não é possível inativar a viatura porque ela está vinculada a uma Ordem de Serviço em andamento.");
        }

        vehicle.setPrefix(dto.getPrefix());
        vehicle.setType(dto.getType());
        vehicle.setFuelType(dto.getFuelType());
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

        return new VehicleResponseDTO(repository.save(vehicle));
    }

    public void delete(Long id) {
        repository.delete(findVehicle(id));
    }

    /**
     * Synchronizes the vehicle km with the arrival km of its last finished service order.
     */
    public void updateCurrentKm(Long vehicleId) {
        ServiceOrder last = serviceOrderRepository
                .findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(vehicleId);

        if (last != null && last.getArrivalKm() != null) {
            Vehicle vehicle = findVehicle(vehicleId);
            vehicle.setKm(last.getArrivalKm().doubleValue());
            repository.save(vehicle);
        }
    }

    public Double calculateAverageConsumption(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found");
        }
        return consumptionCalculator.averageKmPerLiter(refuelingRepository.findByVehicleId(id)).doubleValue();
    }

    public VehicleResponseDTO updateOilChangeKm(Long id, BigDecimal oilChangeKm) {
        Vehicle vehicle = findVehicle(id);
        vehicle.setKmTrocaOleo(oilChangeKm);
        return new VehicleResponseDTO(repository.save(vehicle));
    }

    private Vehicle findVehicle(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    }

    private Model findModel(Long modelId) {
        return modelRepository.findById(modelId)
                .orElseThrow(() -> new ResourceNotFoundException("Model not found!"));
    }
}
