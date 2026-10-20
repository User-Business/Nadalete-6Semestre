package com.example.atlazdb.controller;

import static com.example.atlazdb.support.Fixtures.refueling;
import static com.example.atlazdb.support.Fixtures.serviceOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.atlazdb.dto.ServiceOrderRequestDTO;
import com.example.atlazdb.enums.OccurrenceType;
import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.model.ServiceOrder;
import com.example.atlazdb.service.GenerateCsv;
import com.example.atlazdb.service.RefuelingService;
import com.example.atlazdb.service.ServiceOrderService;

@ExtendWith(MockitoExtension.class)
class ServiceOrderControllerTest {

    private static final LocalDate START = LocalDate.of(2026, 4, 1);
    private static final LocalDate END = LocalDate.of(2026, 4, 30);
    private static final byte[] CSV = "csv".getBytes(StandardCharsets.UTF_8);

    private final ServiceOrder order = serviceOrder(1L, "100", null);
    private final Refueling refueling = refueling(2L, null, "10");

    @Mock
    private ServiceOrderService service;
    @Mock
    private RefuelingService refuelingService;
    @Mock
    private GenerateCsv generateCsv;

    @InjectMocks
    private ServiceOrderController controller;

    @Test
    void shouldDelegateCrudOperationsToService() {
        ServiceOrderRequestDTO request = new ServiceOrderRequestDTO();
        when(service.listAll()).thenReturn(List.of(order));
        when(service.findById(1L)).thenReturn(order);
        when(service.findByUserId(3L)).thenReturn(List.of(order));
        when(service.save(request)).thenReturn(order);
        when(service.update(1L, request)).thenReturn(order);
        when(service.findByMonthAndYear(4, 2026)).thenReturn(List.of(order));
        when(service.countServiceOrdersByDateInterval(START, END)).thenReturn(4L);

        assertThat(controller.list()).containsExactly(order);
        assertThat(controller.findById(1L)).isSameAs(order);
        assertThat(controller.findByUserId(3L).getBody()).containsExactly(order);
        assertThat(controller.create(request)).isSameAs(order);
        assertThat(controller.update(1L, request)).isSameAs(order);
        assertThat(controller.findByMonth(4, 2026).getBody()).containsExactly(order);
        assertThat(controller.countByInterval(START, END).getBody()).isEqualTo(4L);
        controller.delete(1L);
        verify(service).delete(1L);
    }

    @Test
    void shouldListOccurrenceTypes() {
        assertThat(controller.listTypes()).containsExactly(OccurrenceType.values());
    }

    @Test
    void shouldExportRecordsOfDateIntervalAsCsvAttachment() {
        when(service.findByInterval(START, END)).thenReturn(List.of(order));
        when(refuelingService.findByInterval(START, END)).thenReturn(List.of(refueling));
        when(generateCsv.generateCSV(List.of(order), List.of(refueling))).thenReturn(CSV);

        ResponseEntity<byte[]> response = controller.downloadCSV(START, END, null, null, null);

        assertThat(response.getBody()).isEqualTo(CSV);
        assertThat(response.getHeaders().getFirst("Content-Disposition")).isEqualTo("attachment; filename=records.csv");
        assertThat(response.getHeaders().getFirst("Content-Type")).isEqualTo("text/csv; charset=UTF-8");
    }

    @Test
    void shouldExportRecordsOfMonthWhenIntervalIsIncomplete() {
        when(service.findByMonthAndYear(4, 2026)).thenReturn(List.of(order));
        when(refuelingService.findByMonthAndYear(4, 2026)).thenReturn(List.of(refueling));
        when(generateCsv.generateCSV(List.of(order), List.of(refueling))).thenReturn(CSV);

        assertThat(controller.downloadCSV(START, null, 4, 2026, null).getBody()).isEqualTo(CSV);
    }

    @Test
    void shouldExportAllRecordsWhenMonthIsIncomplete() {
        mockAllRecords();
        when(generateCsv.generateCSV(List.of(order), List.of(refueling))).thenReturn(CSV);

        assertThat(controller.downloadCSV(null, END, 4, null, null).getBody()).isEqualTo(CSV);
    }

    @Test
    void shouldExportOnlyRefuelingsWhenCategoryIsRefueling() {
        mockAllRecords();
        when(generateCsv.generateCSV(List.of(), List.of(refueling))).thenReturn(CSV);

        assertThat(controller.downloadCSV(null, null, null, null, "abastecimento").getBody()).isEqualTo(CSV);
    }

    @Test
    void shouldExportOnlyServiceOrdersWhenCategoryIsServiceOrder() {
        mockAllRecords();
        when(generateCsv.generateCSV(List.of(order), List.of())).thenReturn(CSV);

        assertThat(controller.downloadCSV(null, null, null, null, "os").getBody()).isEqualTo(CSV);
    }

    @Test
    void shouldExportEverythingWhenCategoryIsUnknown() {
        mockAllRecords();
        when(generateCsv.generateCSV(List.of(order), List.of(refueling))).thenReturn(CSV);

        assertThat(controller.downloadCSV(null, null, null, null, "other").getBody()).isEqualTo(CSV);
    }

    private void mockAllRecords() {
        when(service.listAll()).thenReturn(List.of(order));
        when(refuelingService.listAll()).thenReturn(List.of(refueling));
    }
}
