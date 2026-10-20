package com.example.atlazdb.service;

import static com.example.atlazdb.support.Fixtures.refueling;
import static com.example.atlazdb.support.Fixtures.serviceOrder;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.model.ServiceOrder;

class GenerateCsvTest {

    private static final String HEADER =
            "vehicle,service_type,justification,requester,destination,departure_km,arrival_km,"
                    + "departure_date,return_date,liters,total_value,receipt\n";

    private final GenerateCsv generateCsv = new GenerateCsv();

    @Test
    void shouldGenerateOnlyHeaderWhenThereAreNoRecords() {
        String csv = toText(generateCsv.generateCSV(List.of(), List.of()));

        assertThat(csv).isEqualTo(HEADER);
    }

    @Test
    void shouldJoinServiceOrderWithItsRefuelingAndEscapeSpecialCharacters() {
        ServiceOrder order = serviceOrder(7L, "100", "250");
        order.setJustification("needs, comma");
        order.setRequester("say \"hi\"");
        order.setDestinationLocation("line\nbreak");
        order.setReturnDate(LocalDateTime.of(2026, 4, 2, 18, 0));

        Refueling unrelated = refueling(1L, serviceOrder(99L, "1", "2"), "5");
        Refueling related = refueling(2L, order, "30.5");

        String csv = toText(generateCsv.generateCSV(List.of(order), List.of(unrelated, related)));

        assertThat(csv).isEqualTo(HEADER
                + "US02,ADMINISTRATIVO,\"needs, comma\",\"say \"\"hi\"\"\",\"line\nbreak\",100,250,"
                + "2026-04-02T08:00,2026-04-02T18:00,30.5,150.00,NF-2\n");
    }

    @Test
    void shouldLeaveRefuelingColumnsEmptyWhenServiceOrderHasNoRefueling() {
        ServiceOrder order = serviceOrder(7L, "100", null);
        Refueling withoutOrder = refueling(1L, null, "5");
        withoutOrder.setVehicle(null);

        String csv = toText(generateCsv.generateCSV(List.of(order), List.of(withoutOrder)));

        assertThat(csv).isEqualTo(HEADER
                + "US02,ADMINISTRATIVO,,Requester,Taubate,100,,2026-04-02T08:00,,,,\n"
                + ",ABASTECIMENTO,,,,,,2026-04-02T08:00,,5,150.00,NF-1\n");
    }

    @Test
    void shouldListStandaloneRefuelingsWithTheirVehicle() {
        Refueling standalone = refueling(3L, null, "12");

        String csv = toText(generateCsv.generateCSV(List.of(), List.of(standalone)));

        assertThat(csv).isEqualTo(HEADER + "US02,ABASTECIMENTO,,,,,,2026-04-02T08:00,,12,150.00,NF-3\n");
    }

    private static String toText(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
