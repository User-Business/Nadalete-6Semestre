package com.example.atlazdb.service;

import static com.example.atlazdb.support.Fixtures.refueling;
import static com.example.atlazdb.support.Fixtures.serviceOrder;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.atlazdb.model.Refueling;

class ConsumptionCalculatorTest {

    private final ConsumptionCalculator calculator = new ConsumptionCalculator();

    @Test
    void shouldReturnZeroWhenThereAreNoRefuelings() {
        assertThat(calculator.averageKmPerLiter(List.of())).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldIgnoreRefuelingsWithoutCompleteTripData() {
        List<Refueling> refuelings = List.of(
                refueling(1L, null, "10"),
                refueling(2L, serviceOrder(1L, "100", null), "10"),
                refueling(3L, serviceOrder(2L, null, "200"), "10"),
                refueling(4L, serviceOrder(3L, "100", "200"), null),
                refueling(5L, serviceOrder(4L, "100", "200"), "0"),
                refueling(6L, serviceOrder(5L, "300", "200"), "10"));

        assertThat(calculator.averageKmPerLiter(refuelings)).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldCalculateAverageKmPerLiterRoundedToOneDecimal() {
        List<Refueling> refuelings = List.of(
                refueling(1L, serviceOrder(1L, "100", "200"), "10"),
                refueling(2L, serviceOrder(2L, "0", "150"), "10"),
                refueling(3L, null, "10"));

        assertThat(calculator.averageKmPerLiter(refuelings)).isEqualTo(new BigDecimal("12.5"));
    }
}
