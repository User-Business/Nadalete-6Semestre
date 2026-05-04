package com.example.AtlazDB.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.AtlazDB.dto.DashboardResponseDTO;
import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.repository.RefuelingRepository;
import com.example.AtlazDB.repository.ServiceOrderRepository;
import com.example.AtlazDB.repository.UserRepository;
import com.example.AtlazDB.repository.VehicleRepository;
import com.example.AtlazDB.repository.projection.AtividadeProjection;

@Service
public class DashboardService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final RefuelingRepository refuelingRepository;

    public DashboardService(
        VehicleRepository vehicleRepository,
        UserRepository userRepository,
        ServiceOrderRepository serviceOrderRepository,
        RefuelingRepository refuelingRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.refuelingRepository = refuelingRepository;
    }

    public DashboardResponseDTO getDashboard() {
        return new DashboardResponseDTO(
            new DashboardResponseDTO.DataContainer(
                buildMetricas(),
                buildAtividadesHoje()
            )
        );
    }

    private DashboardResponseDTO.Metricas buildMetricas() {
        return new DashboardResponseDTO.Metricas(
            (int) vehicleRepository.countByVehicleStatus(VehicleStatus.DISPONIVEL),
            (int) vehicleRepository.count(),
            (int) userRepository.countByProfileAndUserStatus(Profile.TECNICO, UserStatus.EM_CAMPO),
            calcularConsumoMedio()
        );
    }

    private BigDecimal calcularConsumoMedio() {
    List<Refueling> abastecimentos = refuelingRepository.findAll();

    List<Double> consumos = abastecimentos.stream()
            .filter(a -> a.getServiceOrder() != null
                    && a.getServiceOrder().getArrivalKm() != null
                    && a.getServiceOrder().getDepartureKm() != null
                    && a.getLiters() != null
                    && a.getLiters().doubleValue() > 0)
            .map(a -> {
                double km = a.getServiceOrder().getArrivalKm().doubleValue()
                        - a.getServiceOrder().getDepartureKm().doubleValue();
                return km / a.getLiters().doubleValue();
            })
            .filter(c -> c > 0)
            .toList();

        if (consumos.isEmpty()) return BigDecimal.ZERO;

        double media = consumos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        return BigDecimal.valueOf(media).setScale(1, RoundingMode.HALF_UP);
    }
    private List<DashboardResponseDTO.Atividade> buildAtividadesHoje() {
        List<DashboardResponseDTO.Atividade> atividades = new ArrayList<>();

        // adiciona ordens de serviço de hoje
        serviceOrderRepository.findOrdensDeHoje()
            .stream()
            .map(this::toAtividade)
            .forEach(atividades::add);

        // adiciona abastecimentos de hoje
        refuelingRepository.findAbastecimentosDeHoje()
            .stream()
            .map(this::toAtividade)
            .forEach(atividades::add);

        return atividades;
    }

    private DashboardResponseDTO.Atividade toAtividade(AtividadeProjection p) {
        return new DashboardResponseDTO.Atividade(
            p.getId(),
            p.getTipo(),
            p.getPrefixoViatura(),
            p.getNomeTecnico(),
            p.getDescricao(),
            p.getStatus(),
            p.getCorStatus()
        );
    }
}