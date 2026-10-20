package com.example.atlazdb.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.atlazdb.dto.DashboardResponseDTO;
import com.example.atlazdb.enums.Profile;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.repository.RefuelingRepository;
import com.example.atlazdb.repository.ServiceOrderRepository;
import com.example.atlazdb.repository.UserRepository;
import com.example.atlazdb.repository.VehicleRepository;
import com.example.atlazdb.repository.projection.AtividadeProjection;

@Service
public class DashboardService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final RefuelingRepository refuelingRepository;
    private final ConsumptionCalculator consumptionCalculator;

    public DashboardService(VehicleRepository vehicleRepository,
                            UserRepository userRepository,
                            ServiceOrderRepository serviceOrderRepository,
                            RefuelingRepository refuelingRepository,
                            ConsumptionCalculator consumptionCalculator) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.refuelingRepository = refuelingRepository;
        this.consumptionCalculator = consumptionCalculator;
    }

    public DashboardResponseDTO getDashboard() {
        return new DashboardResponseDTO(
                new DashboardResponseDTO.DataContainer(
                        buildMetrics(),
                        buildVehicleKm(),
                        buildTodayActivities()
                )
        );
    }

    private DashboardResponseDTO.Metrics buildMetrics() {
        return new DashboardResponseDTO.Metrics(
                Math.toIntExact(vehicleRepository.countByVehicleStatus(VehicleStatus.EM_USO)),
                Math.toIntExact(vehicleRepository.countByVehicleStatus(VehicleStatus.DISPONIVEL)),
                Math.toIntExact(userRepository.countByProfileAndUserStatus(Profile.TECNICO, UserStatus.EM_CAMPO)),
                consumptionCalculator.averageKmPerLiter(refuelingRepository.findAll()),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private List<DashboardResponseDTO.VehicleKm> buildVehicleKm() {
        // Not implemented yet: waiting for the km history queries
        return new ArrayList<>();
    }

    private List<DashboardResponseDTO.TodayActivity> buildTodayActivities() {
        List<DashboardResponseDTO.TodayActivity> activities = new ArrayList<>();
        serviceOrderRepository.findOrdensDeHoje().stream()
                .map(this::toTodayActivity)
                .forEach(activities::add);
        refuelingRepository.findAbastecimentosDeHoje().stream()
                .map(this::toTodayActivity)
                .forEach(activities::add);
        return activities;
    }

    private DashboardResponseDTO.TodayActivity toTodayActivity(AtividadeProjection projection) {
        return new DashboardResponseDTO.TodayActivity(
                projection.getId(),
                projection.getTipo(),
                projection.getPrefixoViatura(),
                projection.getNomeTecnico(),
                projection.getDescricao(),
                projection.getStatus(),
                projection.getCorStatus()
        );
    }
}
