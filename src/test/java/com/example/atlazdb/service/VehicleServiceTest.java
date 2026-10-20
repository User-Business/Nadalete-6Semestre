package com.example.atlazdb.service;

import static com.example.atlazdb.support.Fixtures.model;
import static com.example.atlazdb.support.Fixtures.refueling;
import static com.example.atlazdb.support.Fixtures.serviceOrder;
import static com.example.atlazdb.support.Fixtures.user;
import static com.example.atlazdb.support.Fixtures.vehicle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.atlazdb.dto.VehicleRequestDTO;
import com.example.atlazdb.dto.VehicleResponseDTO;
import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.enums.VehicleType;
import com.example.atlazdb.exception.BusinessRuleException;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.model.User;
import com.example.atlazdb.model.Vehicle;
import com.example.atlazdb.repository.ModelRepository;
import com.example.atlazdb.repository.RefuelingRepository;
import com.example.atlazdb.repository.ServiceOrderRepository;
import com.example.atlazdb.repository.UserRepository;
import com.example.atlazdb.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository repository;
    @Mock
    private ModelRepository modelRepository;
    @Mock
    private ServiceOrderRepository serviceOrderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefuelingRepository refuelingRepository;
    @Mock
    private ConsumptionCalculator consumptionCalculator;

    @InjectMocks
    private VehicleService service;

    @Test
    void shouldListOnlyAvailableVehiclesCompatibleWithUserLicenses() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, CnhType.B)));
        when(repository.findAll()).thenReturn(List.of(
                vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B),
                vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.D),
                vehicle(3L, VehicleStatus.EM_USO, CnhType.B)));

        List<VehicleResponseDTO> available = service.findAvailableVehiclesByUser(1L);

        assertThat(available).extracting(VehicleResponseDTO::id).containsExactly(1L);
    }

    @Test
    void shouldListNoVehicleForUserWithoutLicenses() {
        User user = user(1L);
        user.setCnhTypes(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findAll()).thenReturn(List.of(vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B)));

        assertThat(service.findAvailableVehiclesByUser(1L)).isEmpty();
    }

    @Test
    void shouldFailListingAvailableVehiclesOfUnknownUser() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findAvailableVehiclesByUser(9L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    void shouldListAllVehiclesAsResponses() {
        when(repository.findAll()).thenReturn(List.of(vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B)));

        assertThat(service.listAll()).containsExactly(new VehicleResponseDTO(1L, "US01", "Prisma", "Chevrolet",
                VehicleStatus.DISPONIVEL, FuelType.FLEX, VehicleType.UTILITARIO, 1000.0, 10L, CnhType.B,
                new BigDecimal("11000")));
    }

    @Test
    void shouldFindVehicleById() {
        when(repository.findById(1L)).thenReturn(Optional.of(vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B)));

        assertThat(service.findById(1L).prefix()).isEqualTo("US01");
    }

    @Test
    void shouldThrowNotFoundWhenVehicleDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Vehicle not found");
    }

    @Test
    void shouldCreateVehicleApplyingDefaults() {
        when(modelRepository.findById(10L)).thenReturn(Optional.of(model(10L)));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponseDTO saved = service.save(request(null, null, null, null));

        assertThat(saved).extracting(VehicleResponseDTO::prefix, VehicleResponseDTO::tipoCnhNecessaria,
                        VehicleResponseDTO::status, VehicleResponseDTO::km, VehicleResponseDTO::kmTrocaOleo)
                .containsExactly("AB-1", CnhType.B, VehicleStatus.DISPONIVEL, 0.0, BigDecimal.valueOf(10000.0));
    }

    @Test
    void shouldCreateVehicleWithInformedValues() {
        when(modelRepository.findById(10L)).thenReturn(Optional.of(model(10L)));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponseDTO saved = service.save(request(VehicleStatus.MANUTENCAO, CnhType.D, 500.0, 900.0));

        assertThat(saved).extracting(VehicleResponseDTO::tipoCnhNecessaria, VehicleResponseDTO::status,
                        VehicleResponseDTO::km, VehicleResponseDTO::kmTrocaOleo)
                .containsExactly(CnhType.D, VehicleStatus.MANUTENCAO, 500.0, BigDecimal.valueOf(900.0));
    }

    @Test
    void shouldFailCreatingVehicleWithUnknownModel() {
        when(modelRepository.findById(10L)).thenReturn(Optional.empty());
        VehicleRequestDTO request = request(null, null, null, null);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Model not found!");
    }

    @Test
    void shouldUpdateOnlyInformedOptionalFields() {
        Vehicle vehicle = vehicle(1L, VehicleStatus.EM_USO, CnhType.C);
        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(modelRepository.findById(10L)).thenReturn(Optional.of(model(10L)));
        when(repository.save(vehicle)).thenReturn(vehicle);

        VehicleResponseDTO updated = service.update(1L, request(null, null, null, null));

        assertThat(updated).extracting(VehicleResponseDTO::prefix, VehicleResponseDTO::status,
                        VehicleResponseDTO::km, VehicleResponseDTO::tipoCnhNecessaria)
                .containsExactly("AB-1", VehicleStatus.EM_USO, 1000.0, CnhType.C);
    }

    @Test
    void shouldUpdateAllInformedFields() {
        Vehicle vehicle = vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B);
        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(modelRepository.findById(10L)).thenReturn(Optional.of(model(10L)));
        when(repository.save(vehicle)).thenReturn(vehicle);

        VehicleResponseDTO updated = service.update(1L, request(VehicleStatus.MANUTENCAO, CnhType.E, 2000.0, null));

        assertThat(updated).extracting(VehicleResponseDTO::status, VehicleResponseDTO::km,
                        VehicleResponseDTO::tipoCnhNecessaria)
                .containsExactly(VehicleStatus.MANUTENCAO, 2000.0, CnhType.E);
    }

    @Test
    void shouldDeactivateVehicleWithoutTripInProgress() {
        Vehicle vehicle = vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B);
        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(modelRepository.findById(10L)).thenReturn(Optional.of(model(10L)));
        when(serviceOrderRepository.existsByVehicleIdAndReturnDateIsNull(1L)).thenReturn(false);
        when(repository.save(vehicle)).thenReturn(vehicle);

        VehicleResponseDTO updated = service.update(1L, request(VehicleStatus.DESATIVADA, null, null, null));

        assertThat(updated.status()).isEqualTo(VehicleStatus.DESATIVADA);
    }

    @Test
    void shouldNotDeactivateVehicleWithTripInProgress() {
        when(repository.findById(1L)).thenReturn(Optional.of(vehicle(1L, VehicleStatus.EM_USO, CnhType.B)));
        when(modelRepository.findById(10L)).thenReturn(Optional.of(model(10L)));
        when(serviceOrderRepository.existsByVehicleIdAndReturnDateIsNull(1L)).thenReturn(true);
        VehicleRequestDTO request = request(VehicleStatus.DESATIVADA, null, null, null);

        assertThatThrownBy(() -> service.update(1L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Ordem de Serviço em andamento");
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteExistingVehicle() {
        Vehicle vehicle = vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B);
        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));

        service.delete(1L);

        verify(repository).delete(vehicle);
    }

    @Test
    void shouldSyncVehicleKmWithLastFinishedTrip() {
        Vehicle vehicle = vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B);
        when(serviceOrderRepository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(1L))
                .thenReturn(serviceOrder(3L, "1000", "1350"));
        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));

        service.updateCurrentKm(1L);

        assertThat(vehicle.getKm()).isEqualTo(1350.0);
        verify(repository).save(vehicle);
    }

    @Test
    void shouldNotSyncVehicleKmWithoutFinishedTrip() {
        when(serviceOrderRepository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(1L))
                .thenReturn(null);

        service.updateCurrentKm(1L);

        verify(repository, never()).save(any());
    }

    @Test
    void shouldNotSyncVehicleKmWhenLastTripHasNoArrivalKm() {
        when(serviceOrderRepository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(1L))
                .thenReturn(serviceOrder(3L, "1000", null));

        service.updateCurrentKm(1L);

        verify(repository, never()).save(any());
    }

    @Test
    void shouldCalculateAverageConsumptionOfVehicle() {
        List<Refueling> refuelings = List.of(refueling(1L, null, "10"));
        when(repository.existsById(1L)).thenReturn(true);
        when(refuelingRepository.findByVehicleId(1L)).thenReturn(refuelings);
        when(consumptionCalculator.averageKmPerLiter(refuelings)).thenReturn(new BigDecimal("11.4"));

        assertThat(service.calculateAverageConsumption(1L)).isEqualTo(11.4);
    }

    @Test
    void shouldFailCalculatingConsumptionOfUnknownVehicle() {
        when(repository.existsById(9L)).thenReturn(false);

        assertThatThrownBy(() -> service.calculateAverageConsumption(9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdateOilChangeKm() {
        Vehicle vehicle = vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B);
        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(repository.save(vehicle)).thenReturn(vehicle);

        VehicleResponseDTO updated = service.updateOilChangeKm(1L, new BigDecimal("15000"));

        assertThat(updated.kmTrocaOleo()).isEqualByComparingTo("15000");
    }

    private static VehicleRequestDTO request(VehicleStatus status, CnhType license, Double km, Double oilChangeKm) {
        VehicleRequestDTO dto = new VehicleRequestDTO();
        dto.setPrefix("AB-1");
        dto.setType(VehicleType.PASSEIO);
        dto.setModelId(10L);
        dto.setFuelType(FuelType.ETANOL);
        dto.setStatus(status);
        dto.setTipoCnhNecessaria(license);
        dto.setKm(km);
        dto.setKmTrocaOleo(oilChangeKm);
        return dto;
    }
}
