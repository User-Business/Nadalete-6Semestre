package com.example.atlazdb.controller;

import static com.example.atlazdb.support.Fixtures.vehicle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.atlazdb.dto.VehicleRequestDTO;
import com.example.atlazdb.dto.VehicleResponseDTO;
import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.service.VehicleService;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    private final VehicleResponseDTO vehicle = new VehicleResponseDTO(vehicle(1L, VehicleStatus.DISPONIVEL, CnhType.B));

    @Mock
    private VehicleService service;

    @InjectMocks
    private VehicleController controller;

    @Test
    void shouldDelegateQueriesToService() {
        when(service.listAll()).thenReturn(List.of(vehicle));
        when(service.findById(1L)).thenReturn(vehicle);
        when(service.findAvailableVehiclesByUser(5L)).thenReturn(List.of(vehicle));
        when(service.calculateAverageConsumption(1L)).thenReturn(12.3);

        assertThat(controller.list().getBody()).containsExactly(vehicle);
        assertThat(controller.findById(1L).getBody()).isEqualTo(vehicle);
        assertThat(controller.listAvailableVehicles(5L)).containsExactly(vehicle);
        assertThat(controller.getAverageConsumption(1L).getBody()).isEqualTo(12.3);
        assertThat(controller.getVehicleStatuses().getBody()).containsExactly(VehicleStatus.values());
    }

    @Test
    void shouldDelegateCommandsToService() {
        VehicleRequestDTO request = new VehicleRequestDTO();
        BigDecimal oilChangeKm = new BigDecimal("20000");
        when(service.save(request)).thenReturn(vehicle);
        when(service.update(1L, request)).thenReturn(vehicle);
        when(service.updateOilChangeKm(1L, oilChangeKm)).thenReturn(vehicle);

        assertThat(controller.create(request).getBody()).isEqualTo(vehicle);
        assertThat(controller.update(1L, request).getBody()).isEqualTo(vehicle);
        assertThat(controller.updateOilChangeKm(1L, oilChangeKm).getBody()).isEqualTo(vehicle);
    }

    @Test
    void shouldAnswerNoContentWhenVehicleIsDeleted() {
        ResponseEntity<Void> response = controller.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(1L);
    }
}
