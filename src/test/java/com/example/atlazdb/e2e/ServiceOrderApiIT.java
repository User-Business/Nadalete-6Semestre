package com.example.atlazdb.e2e;

import static com.example.atlazdb.e2e.JsonSchemas.arrayOf;
import static com.example.atlazdb.e2e.JsonSchemas.schema;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceOrderApiIT extends AbstractApiIT {

    private static final LocalDateTime DEPARTURE = LocalDateTime.of(2026, 4, 2, 8, 0);
    private static final LocalDateTime RETURN = LocalDateTime.of(2026, 4, 2, 18, 0);
    private static final String CSV_HEADER = "vehicle,service_type,justification,requester,destination,departure_km,"
            + "arrival_km,departure_date,return_date,liters,total_value,receipt\n";

    private int userId;
    private int vehicleId;

    @BeforeEach
    void createUserAndVehicle() {
        userId = api.createTechnician("tech@atlaz.com");
        vehicleId = api.createVehicle("V-01", api.createModel("Hilux", "Toyota"), "B", 1000);
    }

    @Test
    void shouldOpenServiceOrderAndMarkVehicleAndUserAsBusy() {
        json(orderBody(userId, vehicleId, ""))
                .when().post("/service-orders")
                .then().statusCode(200)
                .body(schema("service-order"))
                .body("id", equalTo(1))
                .body("serviceType", equalTo("INSPECAO"))
                .body("destinationLocation", equalTo("Taubate"))
                .body("departureKm", numericValue("1000"))
                .body("departureDate", equalTo("2026-04-02T08:00:00"))
                .body("arrivalKm", nullValue())
                .body("returnDate", nullValue())
                .body("user.id", equalTo(userId))
                .body("vehicle.id", equalTo(vehicleId));

        when().get("/vehicles/{id}", vehicleId).then().statusCode(200).body("status", equalTo("EM_USO"));
        when().get("/users/{id}", userId).then().statusCode(200).body("userStatus", equalTo("EM_CAMPO"));
    }

    @Test
    void shouldFinishTripReleasingVehicleAndUser() {
        int orderId = api.openServiceOrder(userId, vehicleId, DEPARTURE, 1000);

        json("""
                {"arrivalKm": 1250, "returnDate": "%s"}
                """.formatted(RETURN))
                .when().put("/service-orders/{id}", orderId)
                .then().statusCode(200)
                .body(schema("service-order"))
                .body("arrivalKm", numericValue("1250"))
                .body("returnDate", equalTo("2026-04-02T18:00:00"));

        when().get("/vehicles/{id}", vehicleId).then().statusCode(200)
                .body("status", equalTo("DISPONIVEL"))
                .body("km", numericValue("1250"));
        when().get("/users/{id}", userId).then().statusCode(200).body("userStatus", equalTo("DISPONIVEL"));
    }

    @Test
    void shouldRegisterAlreadyFinishedTripUpdatingVehicleKm() {
        json(orderBody(userId, vehicleId, """
                , "arrivalKm": 1100, "returnDate": "%s"
                """.formatted(RETURN)))
                .when().post("/service-orders")
                .then().statusCode(200)
                .body(schema("service-order"))
                .body("arrivalKm", numericValue("1100"));

        when().get("/vehicles/{id}", vehicleId).then().statusCode(200).body("km", numericValue("1100"));
    }

    @Test
    void shouldQueryServiceOrders() {
        int orderId = api.openServiceOrder(userId, vehicleId, DEPARTURE, 1000);

        when().get("/service-orders").then().statusCode(200)
                .body(arrayOf("service-order")).body("id", contains(orderId));
        when().get("/service-orders/{id}", orderId).then().statusCode(200)
                .body(schema("service-order")).body("id", equalTo(orderId));
        when().get("/service-orders/user/{userId}", userId).then().statusCode(200)
                .body(arrayOf("service-order")).body("id", contains(orderId));
        given().queryParam("month", 4).queryParam("year", 2026).when().get("/service-orders/by-month")
                .then().statusCode(200).body(arrayOf("service-order")).body("id", contains(orderId));
        given().queryParam("month", 5).queryParam("year", 2026).when().get("/service-orders/by-month")
                .then().statusCode(200).body(arrayOf("service-order")).body("$", empty());
    }

    @Test
    void shouldCountServiceOrdersOfInterval() {
        api.openServiceOrder(userId, vehicleId, DEPARTURE, 1000);

        given().queryParam("dataInicio", "2026-04-01").queryParam("dataFim", "2026-04-02")
                .when().get("/service-orders/count-by-interval")
                .then().statusCode(200).body(schema("count")).body(equalTo("1"));
        given().queryParam("dataInicio", "2026-04-03").queryParam("dataFim", "2026-04-30")
                .when().get("/service-orders/count-by-interval")
                .then().statusCode(200).body(schema("count")).body(equalTo("0"));
    }

    @Test
    void shouldListOccurrenceTypes() {
        when().get("/service-orders/occurrence-types")
                .then().statusCode(200)
                .body(schema("string-list"))
                .body("$", hasSize(11))
                .body("[0]", equalTo("ADMINISTRATIVO"));
    }

    @Test
    void shouldDeleteServiceOrder() {
        int orderId = api.openServiceOrder(userId, vehicleId, DEPARTURE, 1000);

        when().delete("/service-orders/{id}", orderId).then().statusCode(200);

        assertError(when().get("/service-orders/{id}", orderId).then(), 404, "Service order not found!");
    }

    @Test
    void shouldExportAllRecordsAsCsv() {
        createOrderWithRefuelingAndStandaloneRefueling();

        when().get("/service-orders/csv")
                .then().statusCode(200)
                .header("Content-Type", "text/csv;charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=records.csv")
                .body(equalTo(CSV_HEADER
                        + "V-01,INSPECAO,Routine,Manager,Taubate,1000.00,1250.00,2026-04-02T08:00,2026-04-02T18:00,"
                        + "40.00,230.00,NF-40\n"
                        + "V-01,ABASTECIMENTO,,,,,,2026-05-10T10:00,,15.00,90.00,NF-15\n"));
    }

    @Test
    void shouldExportCsvFilteredByIntervalAndCategory() {
        createOrderWithRefuelingAndStandaloneRefueling();

        given().queryParam("dataInicio", "2026-05-01").queryParam("dataFim", "2026-05-31")
                .when().get("/service-orders/csv")
                .then().statusCode(200)
                .body(equalTo(CSV_HEADER + "V-01,ABASTECIMENTO,,,,,,2026-05-10T10:00,,15.00,90.00,NF-15\n"));
        given().queryParam("month", 4).queryParam("year", 2026).queryParam("categoria", "os")
                .when().get("/service-orders/csv")
                .then().statusCode(200)
                .body(equalTo(CSV_HEADER
                        + "V-01,INSPECAO,Routine,Manager,Taubate,1000.00,1250.00,2026-04-02T08:00,2026-04-02T18:00,,,\n"));
        given().queryParam("categoria", "abastecimento")
                .when().get("/service-orders/csv")
                .then().statusCode(200)
                .body(equalTo(CSV_HEADER + "V-01,ABASTECIMENTO,,,,,,2026-05-10T10:00,,15.00,90.00,NF-15\n"));
    }

    @Test
    void shouldRejectUserWithoutRequiredLicense() {
        int truck = api.createVehicle("TRUCK-1", api.createModel("Atego", "Mercedes"), "D", 0);

        assertError(json(orderBody(userId, truck, "")).when().post("/service-orders").then(), 422,
                "User does not have the required CNH for this vehicle.");
    }

    @Test
    void shouldRejectArrivalKmLowerThanLastFinishedTrip() {
        int orderId = api.openServiceOrder(userId, vehicleId, DEPARTURE, 1000);
        api.finishServiceOrder(orderId, 1250, RETURN);

        assertError(json(orderBody(userId, vehicleId, """
                , "arrivalKm": 1100, "returnDate": "%s"
                """.formatted(RETURN.plusDays(1)))).when().post("/service-orders").then(), 422,
                "Km cannot be less than the last recorded (1250.0)");
    }

    @Test
    void shouldRejectFinishingTripWithKmLowerThanVehicleKm() {
        int orderId = api.openServiceOrder(userId, vehicleId, DEPARTURE, 1000);

        assertError(json("""
                {"arrivalKm": 900, "returnDate": "%s"}
                """.formatted(RETURN)).when().put("/service-orders/{id}", orderId).then(), 422,
                "Cannot reduce vehicle km. Current value: 1000.0");
    }

    @Test
    void shouldReturnNotFoundForUnknownReferences() {
        assertError(json(orderBody(999, vehicleId, "")).when().post("/service-orders").then(), 404,
                "User not found!");
        assertError(json(orderBody(userId, 999, "")).when().post("/service-orders").then(), 404,
                "Vehicle not found!");
        assertError(when().get("/service-orders/{id}", 999).then(), 404, "Service order not found!");
        assertError(json("{}").when().put("/service-orders/{id}", 999).then(), 404, "Service order not found!");
        assertError(when().delete("/service-orders/{id}", 999).then(), 404, "Service order not found!");
    }

    @Test
    void shouldRejectMissingRequiredFields() {
        assertError(json("{}").when().post("/service-orders").then(), 400,
                "departureDate: must not be null; departureKm: must not be null; serviceType: must not be null; "
                        + "userId: must not be null; vehicleId: must not be null");
    }

    @Test
    void shouldRejectInvalidQueryParameters() {
        String invalid = "Invalid request parameters or body";

        assertError(given().queryParam("month", 13).queryParam("year", 2026)
                .when().get("/service-orders/by-month").then(), 400, invalid);
        assertError(when().get("/service-orders/count-by-interval").then(), 400, invalid);
        assertError(given().queryParam("dataInicio", "02/04/2026").queryParam("dataFim", "2026-04-30")
                .when().get("/service-orders/count-by-interval").then(), 400, invalid);
    }

    private void createOrderWithRefuelingAndStandaloneRefueling() {
        int cityId = api.createCity("Taubate", "SP");
        int orderId = api.openServiceOrder(userId, vehicleId, DEPARTURE, 1000);
        api.finishServiceOrder(orderId, 1250, RETURN);
        api.createRefueling(userId, vehicleId, cityId, orderId, DEPARTURE.plusHours(2), "40", "230.00");
        api.createRefueling(userId, vehicleId, cityId, null, LocalDateTime.of(2026, 5, 10, 10, 0), "15", "90.00");
    }

    private static String orderBody(int userId, int vehicleId, String extraFields) {
        return """
                {"serviceType": "INSPECAO", "destinationLocation": "Taubate", "justification": "Routine",
                 "requester": "Manager", "departureKm": 1000, "departureDate": "%s",
                 "userId": %d, "vehicleId": %d %s}
                """.formatted(DEPARTURE, userId, vehicleId, extraFields);
    }
}
