package com.example.AtlazDB.service;

import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.model.ServiceOrder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class GenerateCsv {

    private String escape(Object value) {
        if (value == null) return "";
        String str = value.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    public byte[] generateCSV(List<ServiceOrder> orders, List<Refueling> refuelings) {
        StringBuilder csv = new StringBuilder();
        csv.append("vehicle,service_type,justification,requester,destination,departure_km,arrival_km,departure_date,return_date,liters,total_value,receipt\n");

        for (ServiceOrder so : orders) {
            // Find refueling linked to the service order
            Refueling refueling = refuelings.stream()
                    .filter(r -> r.getServiceOrder() != null && r.getServiceOrder().getId().equals(so.getId()))
                    .findFirst()
                    .orElse(null);

            csv.append(escape(so.getVehicle().getPrefix())).append(",");
            csv.append(escape(so.getServiceType())).append(",");
            csv.append(escape(so.getJustification())).append(",");
            csv.append(escape(so.getRequester())).append(",");
            csv.append(escape(so.getDestinationLocation())).append(",");
            csv.append(escape(so.getDepartureKm())).append(",");
            csv.append(escape(so.getArrivalKm())).append(",");
            csv.append(escape(so.getDepartureDate())).append(",");
            csv.append(escape(so.getReturnDate())).append(",");
            csv.append(refueling != null ? escape(refueling.getLiters()) : "").append(",");
            csv.append(refueling != null ? escape(refueling.getTotalValue()) : "").append(",");
            csv.append(refueling != null ? escape(refueling.getReceiptNumber()) : "").append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}