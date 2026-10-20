package com.example.atlazdb.controller;

import static com.example.atlazdb.support.Fixtures.city;
import static com.example.atlazdb.support.Fixtures.model;
import static com.example.atlazdb.support.Fixtures.refueling;
import static com.example.atlazdb.support.Fixtures.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.atlazdb.dto.CityRequestDTO;
import com.example.atlazdb.dto.DashboardResponseDTO;
import com.example.atlazdb.dto.LoginRequestDTO;
import com.example.atlazdb.dto.LoginResponseDTO;
import com.example.atlazdb.dto.ModelRequestDTO;
import com.example.atlazdb.dto.RefuelingRequestDTO;
import com.example.atlazdb.dto.UserRequestDTO;
import com.example.atlazdb.dto.UserResponseDTO;
import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.FuelType;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.model.City;
import com.example.atlazdb.model.Model;
import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.model.User;
import com.example.atlazdb.service.AuthService;
import com.example.atlazdb.service.CityService;
import com.example.atlazdb.service.DashboardService;
import com.example.atlazdb.service.ModelService;
import com.example.atlazdb.service.RefuelingService;
import com.example.atlazdb.service.UserService;

/**
 * Unit tests of the controllers that only delegate to their services.
 */
class CrudControllersTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    class AuthControllerTest {

        @Mock
        private AuthService authService;
        @InjectMocks
        private AuthController controller;

        @Test
        void shouldReturnLoggedUser() {
            LoginRequestDTO request = new LoginRequestDTO();
            LoginResponseDTO response = new LoginResponseDTO(1L, "ADMIN", "Admin");
            when(authService.login(request)).thenReturn(response);

            assertThat(controller.login(request).getBody()).isEqualTo(response);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class CityControllerTest {

        @Mock
        private CityService service;
        @InjectMocks
        private CityController controller;

        @Test
        void shouldDelegateAllOperationsToService() {
            City city = city(1L);
            CityRequestDTO request = new CityRequestDTO();
            when(service.listAll()).thenReturn(List.of(city));
            when(service.findById(1L)).thenReturn(city);
            when(service.save(request)).thenReturn(city);
            when(service.update(1L, request)).thenReturn(city);

            assertThat(controller.list()).containsExactly(city);
            assertThat(controller.findById(1L)).isSameAs(city);
            assertThat(controller.create(request)).isSameAs(city);
            assertThat(controller.update(1L, request)).isSameAs(city);
            controller.delete(1L);
            verify(service).delete(1L);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class ModelControllerTest {

        @Mock
        private ModelService service;
        @InjectMocks
        private ModelController controller;

        @Test
        void shouldDelegateAllOperationsToService() {
            Model model = model(1L);
            ModelRequestDTO request = new ModelRequestDTO();
            when(service.listAll()).thenReturn(List.of(model));
            when(service.findById(1L)).thenReturn(model);
            when(service.save(request)).thenReturn(model);
            when(service.update(1L, request)).thenReturn(model);

            assertThat(controller.list()).containsExactly(model);
            assertThat(controller.findById(1L)).isSameAs(model);
            assertThat(controller.create(request)).isSameAs(model);
            assertThat(controller.update(1L, request)).isSameAs(model);
            controller.delete(1L);
            verify(service).delete(1L);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class DashboardControllerTest {

        @Mock
        private DashboardService service;
        @InjectMocks
        private DashboardController controller;

        @Test
        void shouldReturnDashboard() {
            DashboardResponseDTO dashboard = new DashboardResponseDTO(null);
            when(service.getDashboard()).thenReturn(dashboard);

            assertThat(controller.getDashboard().getBody()).isSameAs(dashboard);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class RefuelingControllerTest {

        @Mock
        private RefuelingService service;
        @InjectMocks
        private RefuelingController controller;

        @Test
        void shouldDelegateAllOperationsToService() {
            Refueling refueling = refueling(1L, null, "10");
            RefuelingRequestDTO request = new RefuelingRequestDTO();
            when(service.searchWithFilters(FuelType.GNV, BigDecimal.ONE, BigDecimal.TEN)).thenReturn(List.of(refueling));
            when(service.findById(1L)).thenReturn(refueling);
            when(service.save(request)).thenReturn(refueling);
            when(service.update(1L, request)).thenReturn(refueling);
            when(service.findByMonthAndYear(4, 2026)).thenReturn(List.of(refueling));
            when(service.countRefuelingsByDateInterval(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30)))
                    .thenReturn(7L);

            assertThat(controller.search(FuelType.GNV, BigDecimal.ONE, BigDecimal.TEN)).containsExactly(refueling);
            assertThat(controller.findById(1L)).isSameAs(refueling);
            assertThat(controller.create(request)).isSameAs(refueling);
            assertThat(controller.update(1L, request)).isSameAs(refueling);
            assertThat(controller.findByMonth(4, 2026).getBody()).containsExactly(refueling);
            assertThat(controller.countRefuelingsByDate(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30))
                    .getBody()).isEqualTo(7L);
            controller.delete(1L);
            verify(service).delete(1L);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class UserControllerTest {

        @Mock
        private UserService service;
        @InjectMocks
        private UserController controller;

        @Test
        void shouldExposeUsersAsResponsesWithoutPassword() {
            User user = user(1L, CnhType.B);
            UserRequestDTO request = new UserRequestDTO();
            UserResponseDTO expected = new UserResponseDTO(user);
            when(service.listAll()).thenReturn(List.of(user));
            when(service.findById(1L)).thenReturn(user);
            when(service.save(request)).thenReturn(user);
            when(service.update(1L, request)).thenReturn(user);

            assertThat(controller.list()).containsExactly(expected);
            assertThat(controller.findById(1L)).isEqualTo(expected);
            assertThat(controller.create(request)).isEqualTo(expected);
            assertThat(controller.update(1L, request)).isEqualTo(expected);
            controller.delete(1L);
            verify(service).delete(1L);
        }

        @Test
        void shouldListUserStatuses() {
            assertThat(controller.getUserStatuses().getBody()).containsExactly(UserStatus.values());
        }
    }
}
