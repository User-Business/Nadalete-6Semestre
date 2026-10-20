package com.example.atlazdb.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.atlazdb.model.Refueling;
import com.example.atlazdb.model.ServiceOrder;

/**
 * Calculates the average fuel consumption (km per liter) based on refuelings
 * linked to finished service orders.
 */
@Component
public class ConsumptionCalculator {

    public BigDecimal averageKmPerLiter(List<Refueling> refuelings) {
        List<Double> consumptions = refuelings.stream()
                .filter(this::hasCompleteTripData)
                .map(this::kmPerLiter)
                .filter(consumption -> consumption > 0)
                .toList();

        if (consumptions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        double average = consumptions.stream().mapToDouble(Double::doubleValue).sum() / consumptions.size();
        return BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
    }

    private boolean hasCompleteTripData(Refueling refueling) {
        ServiceOrder order = refueling.getServiceOrder();
        return order != null
                && order.getArrivalKm() != null
                && order.getDepartureKm() != null
                && refueling.getLiters() != null
                && refueling.getLiters().signum() > 0;
    }

    private double kmPerLiter(Refueling refueling) {
        ServiceOrder order = refueling.getServiceOrder();
        double km = order.getArrivalKm().doubleValue() - order.getDepartureKm().doubleValue();
        return km / refueling.getLiters().doubleValue();
    }
}
