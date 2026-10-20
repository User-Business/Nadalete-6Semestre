package com.example.atlazdb.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.atlazdb.dto.ServiceOrderRequestDTO;
import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.exception.BusinessRuleException;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.ServiceOrder;
import com.example.atlazdb.model.User;
import com.example.atlazdb.model.Vehicle;
import com.example.atlazdb.repository.ServiceOrderRepository;
import com.example.atlazdb.repository.UserRepository;
import com.example.atlazdb.repository.VehicleRepository;

@Service
public class ServiceOrderService {

    private final ServiceOrderRepository repository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleService vehicleService;

    public ServiceOrderService(ServiceOrderRepository repository,
                               UserRepository userRepository,
                               VehicleRepository vehicleRepository,
                               VehicleService vehicleService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleService = vehicleService;
    }

    public List<ServiceOrder> listAll() {
        return repository.findAll();
    }

    public ServiceOrder findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found!"));
    }

    public List<ServiceOrder> findByUserId(Long userId) {
        return repository.findByUser_IdOrderByDepartureDateDesc(userId);
    }

    public List<ServiceOrder> findByMonthAndYear(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        return repository.findByPeriod(start, start.plusMonths(1));
    }

    public List<ServiceOrder> findByInterval(LocalDate startDate, LocalDate endDate) {
        return repository.findByPeriod(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    public Long countServiceOrdersByDateInterval(LocalDate startDate, LocalDate endDate) {
        return repository.countByDepartureDateBetween(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    public ServiceOrder save(ServiceOrderRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found!"));

        if (!hasRequiredLicense(user, vehicle)) {
            throw new BusinessRuleException("User does not have the required CNH for this vehicle.");
        }
        validateArrivalKmAgainstLastTrip(dto.getVehicleId(), dto.getArrivalKm());

        ServiceOrder order = new ServiceOrder();
        order.setServiceType(dto.getServiceType());
        order.setDestinationLocation(dto.getDestinationLocation());
        order.setJustification(dto.getJustification());
        order.setRequester(dto.getRequester());
        order.setDepartureKm(dto.getDepartureKm());
        order.setDepartureDate(dto.getDepartureDate());
        order.setReturnDate(dto.getReturnDate());
        order.setUser(user);
        order.setVehicle(vehicle);
        if (dto.getArrivalKm() != null && dto.getReturnDate() != null) {
            order.setArrivalKm(dto.getArrivalKm());
        }

        ServiceOrder saved = repository.save(order);

        vehicle.setVehicleStatus(VehicleStatus.EM_USO);
        vehicleRepository.save(vehicle);
        user.setUserStatus(UserStatus.EM_CAMPO);
        userRepository.save(user);

        if (saved.getArrivalKm() != null) {
            vehicleService.updateCurrentKm(dto.getVehicleId());
        }
        return saved;
    }

    /**
     * Finishes a trip: only the arrival fields are updated, and both vehicle and user become available again.
     */
    public ServiceOrder update(Long id, ServiceOrderRequestDTO dto) {
        ServiceOrder order = findById(id);
        Vehicle vehicle = order.getVehicle();

        if (dto.getArrivalKm() != null) {
            double currentKm = vehicle.getKm() != null ? vehicle.getKm() : 0.0;
            if (dto.getArrivalKm().doubleValue() < currentKm) {
                throw new BusinessRuleException("Cannot reduce vehicle km. Current value: " + currentKm);
            }
            if (dto.getReturnDate() != null) {
                order.setArrivalKm(dto.getArrivalKm());
                order.setReturnDate(dto.getReturnDate());
            }
        }

        ServiceOrder updated = repository.save(order);

        vehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        vehicleRepository.save(vehicle);
        User user = order.getUser();
        user.setUserStatus(UserStatus.DISPONIVEL);
        userRepository.save(user);

        vehicleService.updateCurrentKm(vehicle.getId());
        return updated;
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private boolean hasRequiredLicense(User user, Vehicle vehicle) {
        Set<CnhType> licenses = user.getCnhTypes();
        return licenses != null && licenses.contains(vehicle.getTipoCnhNecessaria());
    }

    private void validateArrivalKmAgainstLastTrip(Long vehicleId, BigDecimal arrivalKm) {
        if (arrivalKm == null) {
            return;
        }
        ServiceOrder last = repository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(vehicleId);
        if (last != null && last.getArrivalKm() != null && arrivalKm.compareTo(last.getArrivalKm()) < 0) {
            throw new BusinessRuleException(
                    "Km cannot be less than the last recorded (" + last.getArrivalKm().doubleValue() + ")");
        }
    }
}
