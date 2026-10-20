package com.example.atlazdb.e2e;

import static com.example.atlazdb.e2e.JsonSchemas.arrayOf;
import static com.example.atlazdb.e2e.JsonSchemas.schema;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RefuelingApiIT extends AbstractApiIT {

    private static final LocalDateTime DEPARTURE = LocalDateTime.of(2026, 4, 2, 8, 0);
    private static final LocalDateTime REFUELING_TIME = LocalDateTime.of(2026, 4, 2, 17, 36, 17);
    private static final String NOT_FOUND = "Refueling not found.";

    private int userId;
    private int vehicleId;
    private int cityId;
    private int orderId;

    @BeforeEach
    void createReferences() {
        userId = api.createTechnician("tech@atlaz.com");
        vehicleId = api.createVehicle("V-01", api.createModel("Hilux", "Toyota"), "B", 1000);
        cityId = api.createCity("Taubate", "SP");
        orderId = api.openServiceOrder(userId, vehicleId, DEPARTURE, 1000);
        api.finishServiceOrder(orderId, 1400, DEPARTURE.plusHours(10));
    }

    @Test
    void shouldCreateRefuelingLinkedToFinishedTrip() {
        json(refuelingBody(userId, vehicleId, cityId, orderId, "40"))
                .when().post("/refuelings")
                .then().statusCode(200)
                .body(schema("refueling"))
                .body("id", equalTo(1))
                .body("dateTime", equalTo("2026-04-02T17:36:17"))
                .body("liters", numericValue("40"))
                .body("totalValue", numericValue("230.50"))
                .body("receiptNumber", equalTo("NF-0001"))
                .body("currentKm", numericValue("1400"))
                .body("user.id", equalTo(userId))
                .body("vehicle.id", equalTo(vehicleId))
                .body("city.id", equalTo(cityId))
                .body("serviceOrder.id", equalTo(orderId));
    }

    @Test
    void shouldCreateRefuelingWithoutServiceOrder() {
        json(refuelingBody(userId, vehicleId, cityId, null, "40"))
                .when().post("/refuelings")
                .then().statusCode(200)
                .body(schema("refueling"))
                .body("serviceOrder", nullValue())
                .body("currentKm", nullValue());
    }

    @Test
    void shouldListFindUpdateAndDeleteRefueling() {
        int id = api.createRefueling(userId, vehicleId, cityId, orderId, REFUELING_TIME, "40", "230.50");

        when().get("/refuelings").then().statusCode(200)
                .body(arrayOf("refueling")).body("id", contains(id));
        when().get("/refuelings/{id}", id).then().statusCode(200)
                .body(schema("refueling")).body("receiptNumber", equalTo("NF-40"));

        json(refuelingBody(userId, vehicleId, cityId, null, "35.5"))
                .when().put("/refuelings/{id}", id)
                .then().statusCode(200)
                .body(schema("refueling"))
                .body("liters", numericValue("35.5"))
                .body("serviceOrder", nullValue())
                .body("currentKm", nullValue());

        when().delete("/refuelings/{id}", id).then().statusCode(200);
        assertError(when().get("/refuelings/{id}", id).then(), 404, NOT_FOUND);
    }

    @Test
    void shouldFilterRefuelings() {
        int linked = api.createRefueling(userId, vehicleId, cityId, orderId, REFUELING_TIME, "40", "230.50");
        int standalone = api.createRefueling(userId, vehicleId, cityId, null, REFUELING_TIME, "10", "60.00");

        given().queryParam("totalValue", 100).when().get("/refuelings")
                .then().statusCode(200).body(arrayOf("refueling")).body("id", contains(linked));
        given().queryParam("currentKm", 1000).when().get("/refuelings")
                .then().statusCode(200).body(arrayOf("refueling")).body("id", contains(linked));
        given().queryParam("totalValue", 10).when().get("/refuelings")
                .then().statusCode(200).body("id", contains(linked, standalone));
        // The fuel type is not recorded when refueling, so no refueling matches this filter yet
        given().queryParam("fuelType", "DIESEL").when().get("/refuelings")
                .then().statusCode(200).body(arrayOf("refueling")).body("$", empty());
    }

    @Test
    void shouldQueryRefuelingsByPeriod() {
        int id = api.createRefueling(userId, vehicleId, cityId, orderId, REFUELING_TIME, "40", "230.50");
        api.createRefueling(userId, vehicleId, cityId, null, LocalDateTime.of(2026, 5, 1, 0, 0), "10", "60.00");

        given().queryParam("month", 4).queryParam("year", 2026).when().get("/refuelings/by-month")
                .then().statusCode(200).body(arrayOf("refueling")).body("id", contains(id));
        given().queryParam("month", 1).queryParam("year", 2000).when().get("/refuelings/by-month")
                .then().statusCode(200).body(arrayOf("refueling")).body("$", empty());
        given().queryParam("startDate", "2026-04-01").queryParam("endDate", "2026-05-01")
                .when().get("/refuelings/count-by-date")
                .then().statusCode(200).body(schema("count")).body(equalTo("2"));
    }

    @Test
    void shouldReturnNotFoundForUnknownReferences() {
        assertError(json(refuelingBody(999, vehicleId, cityId, null, "40")).when().post("/refuelings").then(),
                404, "User not found.");
        assertError(json(refuelingBody(userId, 999, cityId, null, "40")).when().post("/refuelings").then(),
                404, "Vehicle not found.");
        assertError(json(refuelingBody(userId, vehicleId, 999, null, "40")).when().post("/refuelings").then(),
                404, "City not found.");
        assertError(json(refuelingBody(userId, vehicleId, cityId, 999, "40")).when().post("/refuelings").then(),
                404, "Service order not found.");
    }

    @Test
    void shouldReturnNotFoundForUnknownRefueling() {
        assertError(when().get("/refuelings/{id}", 999).then(), 404, NOT_FOUND);
        assertError(json(refuelingBody(userId, vehicleId, cityId, null, "40"))
                .when().put("/refuelings/{id}", 999).then(), 404, NOT_FOUND);
        assertError(when().delete("/refuelings/{id}", 999).then(), 404, NOT_FOUND);
    }

    @Test
    void shouldRejectInvalidRefueling() {
        assertError(json(refuelingBody(userId, vehicleId, cityId, null, "-1")).when().post("/refuelings").then(),
                400, "liters: must be greater than 0");
        assertError(json("{}").when().post("/refuelings").then(), 400,
                "cityId: must not be null; dateTime: must not be null; liters: must not be null; "
                        + "receiptNumber: must not be blank; totalValue: must not be null; userId: must not be null; "
                        + "vehicleId: must not be null");
    }

    @Test
    void shouldRejectInvalidQueryParameters() {
        String invalid = "Invalid request parameters or body";

        assertError(given().queryParam("month", 13).queryParam("year", 2026)
                .when().get("/refuelings/by-month").then(), 400, invalid);
        assertError(given().queryParam("fuelType", "WATER").when().get("/refuelings").then(), 400, invalid);
        assertError(given().queryParam("startDate", "2026-04-01")
                .when().get("/refuelings/count-by-date").then(), 400, invalid);
    }

    private static String refuelingBody(int userId, int vehicleId, int cityId, Integer orderId, String liters) {
        return """
                {"dateTime": "%s", "liters": %s, "totalValue": 230.50, "receiptNumber": "NF-0001",
                 "userId": %d, "vehicleId": %d, "cityId": %d, "serviceOrderId": %s}
                """.formatted(REFUELING_TIME, liters, userId, vehicleId, cityId, orderId);
    }
}
