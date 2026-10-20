package com.example.atlazdb.e2e;

import static com.example.atlazdb.e2e.JsonSchemas.schema;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DashboardApiIT extends AbstractApiIT {

    @Test
    void shouldReturnEmptyDashboardWhenThereIsNoActivity() {
        when().get("/dashboard")
                .then().statusCode(200)
                .body(schema("dashboard"))
                .body("data.metrics.activeVehicles", equalTo(0))
                .body("data.metrics.totalVehicles", equalTo(0))
                .body("data.metrics.techniciansInField", equalTo(0))
                .body("data.metrics.averageConsum", numericValue("0"))
                .body("data.metrics.todayExpense", numericValue("0"))
                .body("data.vehicleKm", empty())
                .body("data.todayActivities", empty());
    }

    @Test
    void shouldSummarizeTodayActivitiesAndMetrics() {
        LocalDateTime today = LocalDate.now().atTime(7, 0);
        int modelId = api.createModel("Hilux", "Toyota");
        int cityId = api.createCity("Taubate", "SP");
        int busyTechnician = api.createTechnician("busy@atlaz.com");
        int freeTechnician = api.createTechnician("free@atlaz.com");
        int busyVehicle = api.createVehicle("V-BUSY", modelId, "B", 1000);
        int freeVehicle = api.createVehicle("V-FREE", modelId, "B", 1000);

        api.openServiceOrder(busyTechnician, busyVehicle, today, 1000);
        int finishedOrder = api.openServiceOrder(freeTechnician, freeVehicle, today, 1000);
        api.finishServiceOrder(finishedOrder, 1100, today.plusHours(2));
        api.createRefueling(freeTechnician, freeVehicle, cityId, finishedOrder, today.plusHours(1), "10", "95.50");

        when().get("/dashboard")
                .then().statusCode(200)
                .body(schema("dashboard"))
                .body("data.metrics.activeVehicles", equalTo(1))
                .body("data.metrics.totalVehicles", equalTo(1))
                .body("data.metrics.techniciansInField", equalTo(1))
                .body("data.metrics.averageConsum", numericValue("10.0"))
                .body("data.todayActivities.type", contains("DESLOCAMENTO", "DESLOCAMENTO", "ABASTECIMENTO"))
                .body("data.todayActivities.status", containsInAnyOrder("EM ANDAMENTO", "CONCLUÍDO", "CONCLUÍDO"))
                .body("data.todayActivities.vehiclePrefix", containsInAnyOrder("V-BUSY", "V-FREE", "V-FREE"))
                .body("data.todayActivities[2].description", equalTo("Abastecimento: R$ 95.50"))
                .body("data.todayActivities[2].technicianName", equalTo("User free@atlaz.com"))
                .body("data.todayActivities[2].statusColor", equalTo("EMERALD"));
    }

    @Test
    void shouldRejectUnsupportedHttpMethod() {
        assertError(json("{}").when().post("/dashboard").then(), 405, "HTTP method not supported for this resource");
    }

    @Test
    void shouldReturnNotFoundForUnknownResource() {
        assertError(when().get("/unknown-resource").then(), 404, "Resource not found");
    }
}
