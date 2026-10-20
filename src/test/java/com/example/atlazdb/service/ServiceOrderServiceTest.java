package com.example.atlazdb.service;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.atlazdb.dto.ServiceOrderRequestDTO;
import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.OccurrenceType;
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

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest {

    private static final LocalDateTime RETURN_DATE = LocalDateTime.of(2026, 4, 2, 18, 0);

    @Mock
    private ServiceOrderRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private ServiceOrderService service;

    @Test
    void shouldListAllServiceOrders() {
        List<ServiceOrder> orders = List.of(serviceOrder(1L, "100", null));
        when(repository.findAll()).thenReturn(orders);

        assertThat(service.listAll()).isEqualTo(orders);
    }

    @Test
    void shouldThrowNotFoundWhenServiceOrderDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order not found!");
    }

    @Test
    void shouldListServiceOrdersOfUser() {
        List<ServiceOrder> orders = List.of(serviceOrder(1L, "100", null));
        when(repository.findByUser_IdOrderByDepartureDateDesc(1L)).thenReturn(orders);

        assertThat(service.findByUserId(1L)).isEqualTo(orders);
    }

    @Test
    void shouldSearchWholeMonth() {
        List<ServiceOrder> orders = List.of(serviceOrder(1L, "100", null));
        when(repository.findByPeriod(LocalDateTime.of(2026, 4, 1, 0, 0), LocalDateTime.of(2026, 5, 1, 0, 0)))
                .thenReturn(orders);

        assertThat(service.findByMonthAndYear(4, 2026)).isEqualTo(orders);
    }

    @Test
    void shouldSearchWholeDaysOfInterval() {
        List<ServiceOrder> orders = List.of(serviceOrder(1L, "100", null));
        when(repository.findByPeriod(LocalDateTime.of(2026, 4, 1, 0, 0), LocalDateTime.of(2026, 4, 11, 0, 0)))
                .thenReturn(orders);

        assertThat(service.findByInterval(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 10))).isEqualTo(orders);
    }

    @Test
    void shouldCountServiceOrdersOfInterval() {
        when(repository.countByDepartureDateBetween(LocalDateTime.of(2026, 4, 1, 0, 0),
                LocalDate.of(2026, 4, 10).atTime(23, 59, 59, 999_999_999))).thenReturn(2L);

        assertThat(service.countServiceOrdersByDateInterval(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 10)))
                .isEqualTo(2L);
    }

    @Test
    void shouldOpenServiceOrderAndMarkVehicleAndUserAsBusy() {
        User user = user(1L, CnhType.B);
        Vehicle vehicle = vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.B);
        mockUserAndVehicle(user, vehicle);
        when(repository.save(any(ServiceOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceOrder saved = service.save(request(null, null));

        assertThat(saved).extracting(ServiceOrder::getServiceType, ServiceOrder::getDestinationLocation,
                        ServiceOrder::getJustification, ServiceOrder::getRequester, ServiceOrder::getArrivalKm)
                .containsExactly(OccurrenceType.INSPECAO, "Taubate", "Inspection", "Manager", null);
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getVehicle()).isSameAs(vehicle);
        assertThat(vehicle.getVehicleStatus()).isEqualTo(VehicleStatus.EM_USO);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.EM_CAMPO);
        verify(repository, never()).findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(any());
        verify(vehicleService, never()).updateCurrentKm(any());
    }

    @Test
    void shouldRegisterFinishedTripAndUpdateVehicleKm() {
        mockUserAndVehicle(user(1L, CnhType.B), vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.B));
        when(repository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(2L))
                .thenReturn(serviceOrder(8L, "50", "100"));
        when(repository.save(any(ServiceOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceOrder saved = service.save(request("150", RETURN_DATE));

        assertThat(saved.getArrivalKm()).isEqualByComparingTo("150");
        assertThat(saved.getReturnDate()).isEqualTo(RETURN_DATE);
        verify(vehicleService).updateCurrentKm(2L);
    }

    @Test
    void shouldIgnoreArrivalKmWhenReturnDateIsMissing() {
        mockUserAndVehicle(user(1L, CnhType.B), vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.B));
        when(repository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(2L)).thenReturn(null);
        when(repository.save(any(ServiceOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceOrder saved = service.save(request("150", null));

        assertThat(saved.getArrivalKm()).isNull();
        verify(vehicleService, never()).updateCurrentKm(any());
    }

    @Test
    void shouldAcceptArrivalKmWhenLastTripHasNoArrivalKm() {
        mockUserAndVehicle(user(1L, CnhType.B), vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.B));
        when(repository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(2L))
                .thenReturn(serviceOrder(8L, "50", null));
        when(repository.save(any(ServiceOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceOrder saved = service.save(request("10", RETURN_DATE));

        assertThat(saved.getArrivalKm()).isEqualByComparingTo("10");
    }

    @Test
    void shouldRejectArrivalKmLowerThanLastTrip() {
        mockUserAndVehicle(user(1L, CnhType.B), vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.B));
        when(repository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(2L))
                .thenReturn(serviceOrder(8L, "50", "500"));
        ServiceOrderRequestDTO request = request("400", RETURN_DATE);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Km cannot be less than the last recorded (500.0)");
        verify(repository, never()).save(any());
    }

    @Test
    void shouldRejectUserWithoutRequiredLicense() {
        mockUserAndVehicle(user(1L, CnhType.A), vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.D));
        ServiceOrderRequestDTO request = request(null, null);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("User does not have the required CNH for this vehicle.");
    }

    @Test
    void shouldRejectUserWithoutAnyLicense() {
        User user = user(1L);
        user.setCnhTypes(null);
        mockUserAndVehicle(user, vehicle(2L, VehicleStatus.DISPONIVEL, CnhType.B));
        ServiceOrderRequestDTO request = request(null, null);

        assertThatThrownBy(() -> service.save(request)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ServiceOrderRequestDTO request = request(null, null);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    void shouldFailWhenVehicleDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, CnhType.B)));
        when(vehicleRepository.findById(2L)).thenReturn(Optional.empty());
        ServiceOrderRequestDTO request = request(null, null);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Vehicle not found!");
    }

    @Test
    void shouldFinishTripAndReleaseVehicleAndUser() {
        ServiceOrder order = serviceOrder(5L, "1000", null);
        order.getVehicle().setKm(null);
        when(repository.findById(5L)).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        ServiceOrder updated = service.update(5L, request("1200", RETURN_DATE));

        assertThat(updated.getArrivalKm()).isEqualByComparingTo("1200");
        assertThat(updated.getReturnDate()).isEqualTo(RETURN_DATE);
        assertThat(order.getVehicle().getVehicleStatus()).isEqualTo(VehicleStatus.DISPONIVEL);
        assertThat(order.getUser().getUserStatus()).isEqualTo(UserStatus.DISPONIVEL);
        verify(vehicleService).updateCurrentKm(2L);
    }

    @Test
    void shouldKeepArrivalDataWhenReturnDateIsMissingOnUpdate() {
        ServiceOrder order = serviceOrder(5L, "1000", null);
        when(repository.findById(5L)).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        ServiceOrder updated = service.update(5L, request("1200", null));

        assertThat(updated.getArrivalKm()).isNull();
        assertThat(updated.getReturnDate()).isNull();
    }

    @Test
    void shouldReleaseResourcesWhenNoArrivalKmIsInformed() {
        ServiceOrder order = serviceOrder(5L, "1000", null);
        when(repository.findById(5L)).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        ServiceOrder updated = service.update(5L, request(null, RETURN_DATE));

        assertThat(updated.getArrivalKm()).isNull();
        assertThat(order.getVehicle().getVehicleStatus()).isEqualTo(VehicleStatus.DISPONIVEL);
    }

    @Test
    void shouldRejectArrivalKmLowerThanVehicleKm() {
        ServiceOrder order = serviceOrder(5L, "1000", null);
        when(repository.findById(5L)).thenReturn(Optional.of(order));
        ServiceOrderRequestDTO request = request("999", RETURN_DATE);

        assertThatThrownBy(() -> service.update(5L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Cannot reduce vehicle km. Current value: 1000.0");
    }

    @Test
    void shouldDeleteExistingServiceOrder() {
        ServiceOrder order = serviceOrder(5L, "1000", null);
        when(repository.findById(5L)).thenReturn(Optional.of(order));

        service.delete(5L);

        verify(repository).delete(order);
    }

    private void mockUserAndVehicle(User user, Vehicle vehicle) {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(2L)).thenReturn(Optional.of(vehicle));
    }

    private static ServiceOrderRequestDTO request(String arrivalKm, LocalDateTime returnDate) {
        ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
        dto.setServiceType(OccurrenceType.INSPECAO);
        dto.setDestinationLocation("Taubate");
        dto.setJustification("Inspection");
        dto.setRequester("Manager");
        dto.setDepartureKm(new BigDecimal("1000"));
        dto.setArrivalKm(arrivalKm == null ? null : new BigDecimal(arrivalKm));
        dto.setDepartureDate(LocalDateTime.of(2026, 4, 2, 8, 0));
        dto.setReturnDate(returnDate);
        dto.setUserId(1L);
        dto.setVehicleId(2L);
        return dto;
    }
}
