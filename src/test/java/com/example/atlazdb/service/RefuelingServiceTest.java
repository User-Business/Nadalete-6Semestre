package com.example.atlazdb.service;

import static com.example.atlazdb.support.Fixtures.city;
import static com.example.atlazdb.support.Fixtures.refueling;
import static com.example.atlazdb.support.Fixtures.serviceOrder;
import static com.example.atlazdb.support.Fixtures.user;
import static com.example.atlazdb.support.Fixtures.vehicle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.atlazdb.dto.RefuelingRequestDTO;
import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.model.ServiceOrder;
import com.example.atlazdb.repository.CityRepository;
import com.example.atlazdb.repository.RefuelingRepository;
import com.example.atlazdb.repository.ServiceOrderRepository;
import com.example.atlazdb.repository.UserRepository;
import com.example.atlazdb.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class RefuelingServiceTest {

    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2026, 4, 2, 17, 36);

    @Mock
    private RefuelingRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private RefuelingService service;

    @Test
    void shouldListAllRefuelings() {
        List<Refueling> refuelings = List.of(refueling(1L, null, "10"));
        when(repository.findAll()).thenReturn(refuelings);

        assertThat(service.listAll()).isEqualTo(refuelings);
    }

    @Test
    void shouldFindRefuelingById() {
        Refueling refueling = refueling(1L, null, "10");
        when(repository.findById(1L)).thenReturn(Optional.of(refueling));

        assertThat(service.findById(1L)).isSameAs(refueling);
    }

    @Test
    void shouldThrowNotFoundWhenRefuelingDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Refueling not found.");
    }

    @Test
    void shouldCreateRefuelingLinkedToFinishedServiceOrder() {
        mockReferences();
        ServiceOrder order = serviceOrder(4L, "100", "250");
        when(serviceOrderRepository.findById(4L)).thenReturn(Optional.of(order));
        when(repository.save(any(Refueling.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Refueling saved = service.save(request(4L));

        assertThat(saved.getServiceOrder()).isSameAs(order);
        assertThat(saved.getCurrentKm()).isEqualByComparingTo("250");
        assertThat(saved).extracting(Refueling::getLiters, Refueling::getTotalValue, Refueling::getReceiptNumber,
                        Refueling::getDateTime)
                .containsExactly(new BigDecimal("40"), new BigDecimal("230.50"), "NF-123", DATE_TIME);
        assertThat(saved.getUser().getId()).isEqualTo(1L);
        assertThat(saved.getVehicle().getId()).isEqualTo(2L);
        assertThat(saved.getCity().getId()).isEqualTo(3L);
    }

    @Test
    void shouldCreateRefuelingWithoutServiceOrder() {
        mockReferences();
        when(repository.save(any(Refueling.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Refueling saved = service.save(request(null));

        assertThat(saved.getServiceOrder()).isNull();
        assertThat(saved.getCurrentKm()).isNull();
        verifyNoInteractions(serviceOrderRepository);
    }

    @Test
    void shouldFailWhenLinkedServiceOrderDoesNotExist() {
        mockReferences();
        when(serviceOrderRepository.findById(4L)).thenReturn(Optional.empty());
        RefuelingRequestDTO request = request(4L);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order not found.");
        verify(repository, never()).save(any());
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        RefuelingRequestDTO request = request(null);

        assertThatThrownBy(() -> service.save(request)).hasMessage("User not found.");
    }

    @Test
    void shouldFailWhenVehicleDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(vehicleRepository.findById(2L)).thenReturn(Optional.empty());
        RefuelingRequestDTO request = request(null);

        assertThatThrownBy(() -> service.save(request)).hasMessage("Vehicle not found.");
    }

    @Test
    void shouldFailWhenCityDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(vehicleRepository.findById(2L)).thenReturn(Optional.of(vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.B)));
        when(cityRepository.findById(3L)).thenReturn(Optional.empty());
        RefuelingRequestDTO request = request(null);

        assertThatThrownBy(() -> service.save(request)).hasMessage("City not found.");
    }

    @Test
    void shouldUpdateExistingRefueling() {
        Refueling existing = refueling(5L, serviceOrder(4L, "100", "250"), "10");
        existing.setCurrentKm(new BigDecimal("250"));
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        mockReferences();
        when(repository.save(existing)).thenReturn(existing);

        Refueling updated = service.update(5L, request(null));

        assertThat(updated.getServiceOrder()).isNull();
        assertThat(updated.getCurrentKm()).isNull();
        assertThat(updated.getReceiptNumber()).isEqualTo("NF-123");
    }

    @Test
    void shouldNotUpdateWhenRefuelingDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        RefuelingRequestDTO request = request(null);

        assertThatThrownBy(() -> service.update(9L, request)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDeleteExistingRefueling() {
        Refueling refueling = refueling(5L, null, "10");
        when(repository.findById(5L)).thenReturn(Optional.of(refueling));

        service.delete(5L);

        verify(repository).delete(refueling);
    }

    @Test
    void shouldFilterByFuelTypeFirst() {
        List<Refueling> result = List.of(refueling(1L, null, "10"));
        when(repository.findByFuelType(FuelType.DIESEL)).thenReturn(result);

        assertThat(service.searchWithFilters(FuelType.DIESEL, BigDecimal.ONE, BigDecimal.TEN)).isEqualTo(result);
    }

    @Test
    void shouldFilterByTotalValueWhenFuelTypeIsAbsent() {
        List<Refueling> result = List.of(refueling(1L, null, "10"));
        when(repository.findByTotalValueGreaterThan(BigDecimal.ONE)).thenReturn(result);

        assertThat(service.searchWithFilters(null, BigDecimal.ONE, BigDecimal.TEN)).isEqualTo(result);
    }

    @Test
    void shouldFilterByCurrentKmWhenOnlyKmIsInformed() {
        List<Refueling> result = List.of(refueling(1L, null, "10"));
        when(repository.findByCurrentKmGreaterThan(BigDecimal.TEN)).thenReturn(result);

        assertThat(service.searchWithFilters(null, null, BigDecimal.TEN)).isEqualTo(result);
    }

    @Test
    void shouldListAllWhenNoFilterIsInformed() {
        List<Refueling> result = List.of(refueling(1L, null, "10"));
        when(repository.findAll()).thenReturn(result);

        assertThat(service.searchWithFilters(null, null, null)).isEqualTo(result);
    }

    @Test
    void shouldSearchWholeMonth() {
        List<Refueling> result = List.of(refueling(1L, null, "10"));
        when(repository.findByPeriod(LocalDateTime.of(2026, 12, 1, 0, 0), LocalDateTime.of(2027, 1, 1, 0, 0)))
                .thenReturn(result);

        assertThat(service.findByMonthAndYear(12, 2026)).isEqualTo(result);
    }

    @Test
    void shouldSearchWholeDaysOfInterval() {
        List<Refueling> result = List.of(refueling(1L, null, "10"));
        when(repository.findByPeriod(LocalDateTime.of(2026, 4, 1, 0, 0), LocalDateTime.of(2026, 4, 3, 0, 0)))
                .thenReturn(result);

        assertThat(service.findByInterval(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 2))).isEqualTo(result);
    }

    @Test
    void shouldCountRefuelingsBetweenFirstAndLastInstantOfInterval() {
        when(repository.countByDateTimeBetween(LocalDateTime.of(2026, 4, 1, 0, 0),
                LocalDate.of(2026, 4, 2).atTime(23, 59, 59, 999_999_999))).thenReturn(3L);

        assertThat(service.countRefuelingsByDateInterval(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 2)))
                .isEqualTo(3L);
    }

    private void mockReferences() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(vehicleRepository.findById(2L)).thenReturn(Optional.of(vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.B)));
        when(cityRepository.findById(3L)).thenReturn(Optional.of(city(3L)));
    }

    private static RefuelingRequestDTO request(Long serviceOrderId) {
        RefuelingRequestDTO dto = new RefuelingRequestDTO();
        dto.setDateTime(DATE_TIME);
        dto.setLiters(new BigDecimal("40"));
        dto.setTotalValue(new BigDecimal("230.50"));
        dto.setReceiptNumber("NF-123");
        dto.setUserId(1L);
        dto.setVehicleId(2L);
        dto.setCityId(3L);
        dto.setServiceOrderId(serviceOrderId);
        return dto;
    }
}
