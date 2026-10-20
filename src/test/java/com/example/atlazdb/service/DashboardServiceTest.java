package com.example.atlazdb.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.atlazdb.dto.DashboardResponseDTO;
import com.example.atlazdb.enums.Profile;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.repository.RefuelingRepository;
import com.example.atlazdb.repository.ServiceOrderRepository;
import com.example.atlazdb.repository.UserRepository;
import com.example.atlazdb.repository.VehicleRepository;
import com.example.atlazdb.repository.projection.AtividadeProjection;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ServiceOrderRepository serviceOrderRepository;
    @Mock
    private RefuelingRepository refuelingRepository;
    @Mock
    private ConsumptionCalculator consumptionCalculator;

    @InjectMocks
    private DashboardService service;

    @Test
    void shouldBuildDashboardWithMetricsAndTodayActivities() {
        List<Refueling> refuelings = List.of(new Refueling());
        when(vehicleRepository.countByVehicleStatus(VehicleStatus.EM_USO)).thenReturn(2L);
        when(vehicleRepository.countByVehicleStatus(VehicleStatus.DISPONIVEL)).thenReturn(5L);
        when(userRepository.countByProfileAndUserStatus(Profile.TECNICO, UserStatus.EM_CAMPO)).thenReturn(1L);
        when(refuelingRepository.findAll()).thenReturn(refuelings);
        when(consumptionCalculator.averageKmPerLiter(refuelings)).thenReturn(new BigDecimal("9.8"));
        AtividadeProjection trip = activity(1L, "DESLOCAMENTO", "EM ANDAMENTO", "AMBER");
        AtividadeProjection refueling = activity(2L, "ABASTECIMENTO", "CONCLUÍDO", "EMERALD");
        when(serviceOrderRepository.findOrdensDeHoje()).thenReturn(List.of(trip));
        when(refuelingRepository.findAbastecimentosDeHoje()).thenReturn(List.of(refueling));

        DashboardResponseDTO.DataContainer data = service.getDashboard().data();

        assertThat(data.metrics()).isEqualTo(new DashboardResponseDTO.Metrics(
                2, 5, 1, new BigDecimal("9.8"), new BigDecimal("0.00")));
        assertThat(data.vehicleKm()).isEmpty();
        assertThat(data.todayActivities()).containsExactly(
                new DashboardResponseDTO.TodayActivity(1L, "DESLOCAMENTO", "US01", "John", "Taubate",
                        "EM ANDAMENTO", "AMBER"),
                new DashboardResponseDTO.TodayActivity(2L, "ABASTECIMENTO", "US01", "John", "Taubate",
                        "CONCLUÍDO", "EMERALD"));
    }

    private static AtividadeProjection activity(Long id, String type, String status, String color) {
        AtividadeProjection projection = mock(AtividadeProjection.class);
        when(projection.getId()).thenReturn(id);
        when(projection.getTipo()).thenReturn(type);
        when(projection.getPrefixoViatura()).thenReturn("US01");
        when(projection.getNomeTecnico()).thenReturn("John");
        when(projection.getDescricao()).thenReturn("Taubate");
        when(projection.getStatus()).thenReturn(status);
        when(projection.getCorStatus()).thenReturn(color);
        return projection;
    }
}
