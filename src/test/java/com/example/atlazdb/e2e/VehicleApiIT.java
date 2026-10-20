package com.example.atlazdb.e2e;

import static com.example.atlazdb.e2e.JsonSchemas.arrayOf;
import static com.example.atlazdb.e2e.JsonSchemas.schema;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleApiIT extends AbstractApiIT {

    private static final String NOT_FOUND = "Vehicle not found";
    private static final LocalDateTime DEPARTURE = LocalDateTime.of(2026, 4, 2, 8, 0);

    private int modelId;

    @BeforeEach
    void createModel() {
        modelId = api.createModel("Hilux", "Toyota");
    }

    @Test
    void shouldCreateVehicleApplyingDefaults() {
        json("""
                {"prefix": "V-01", "type": "PASSEIO", "modelId": %d, "fuelType": "ETANOL"}
                """.formatted(modelId))
                .when().post("/vehicles")
                .then().statusCode(200)
                .body(schema("vehicle"))
                .body("id", equalTo(1))
                .body("prefix", equalTo("V-01"))
                .body("model", equalTo("Hilux"))
                .body("brand", equalTo("Toyota"))
                .body("modelId", equalTo(modelId))
                .body("type", equalTo("PASSEIO"))
                .body("fuelType", equalTo("ETANOL"))
                .body("status", equalTo("DISPONIVEL"))
                .body("tipoCnhNecessaria", equalTo("B"))
                .body("km", numericValue("0"))
                .body("kmTrocaOleo", numericValue("10000"));
    }

    @Test
    void shouldCreateVehicleWithInformedValues() {
        json("""
                {"prefix": "T-01", "type": "UTILITARIO", "modelId": %d, "fuelType": "DIESEL",
                 "km": 1500, "status": "MANUTENCAO", "tipoCnhNecessaria": "D", "kmTrocaOleo": 5000}
                """.formatted(modelId))
                .when().post("/vehicles")
                .then().statusCode(200)
                .body(schema("vehicle"))
                .body("status", equalTo("MANUTENCAO"))
                .body("tipoCnhNecessaria", equalTo("D"))
                .body("km", numericValue("1500"))
                .body("kmTrocaOleo", numericValue("5000"));
    }

    @Test
    void shouldListAndFindVehicles() {
        api.createVehicle("V-01", modelId, "B", 0);
        int second = api.createVehicle("V-02", modelId, "C", 250);

        when().get("/vehicles")
                .then().statusCode(200)
                .body(arrayOf("vehicle"))
                .body("prefix", contains("V-01", "V-02"));

        when().get("/vehicles/{id}", second)
                .then().statusCode(200)
                .body(schema("vehicle"))
                .body("prefix", equalTo("V-02"))
                .body("tipoCnhNecessaria", equalTo("C"))
                .body("km", numericValue("250"));
    }

    @Test
    void shouldUpdateVehicle() {
        int id = api.createVehicle("V-01", modelId, "B", 0);
        int otherModel = api.createModel("Onix", "Chevrolet");

        json("""
                {"prefix": "V-01A", "type": "PASSEIO", "modelId": %d, "fuelType": "GASOLINA",
                 "km": 300, "status": "MANUTENCAO"}
                """.formatted(otherModel))
                .when().put("/vehicles/{id}", id)
                .then().statusCode(200)
                .body(schema("vehicle"))
                .body("prefix", equalTo("V-01A"))
                .body("model", equalTo("Onix"))
                .body("status", equalTo("MANUTENCAO"))
                .body("tipoCnhNecessaria", equalTo("B"))
                .body("km", numericValue("300"));
    }

    @Test
    void shouldDeleteVehicle() {
        int id = api.createVehicle("V-01", modelId, "B", 0);

        when().delete("/vehicles/{id}", id).then().statusCode(204);

        assertError(when().get("/vehicles/{id}", id).then(), 404, NOT_FOUND);
    }

    @Test
    void shouldListVehicleStatuses() {
        when().get("/vehicles/vehicle-status")
                .then().statusCode(200)
                .body(schema("string-list"))
                .body("$", contains("DISPONIVEL", "MANUTENCAO", "EM_USO", "DESATIVADA"));
    }

    @Test
    void shouldUpdateOilChangeKm() {
        int id = api.createVehicle("V-01", modelId, "B", 0);

        json("20000").when().patch("/vehicles/{id}/km-troca-oleo", id)
                .then().statusCode(200)
                .body(schema("vehicle"))
                .body("kmTrocaOleo", numericValue("20000"));
    }

    @Test
    void shouldListOnlyAvailableVehiclesCompatibleWithUserLicense() {
        int technician = api.createTechnician("tech@atlaz.com");
        int available = api.createVehicle("V-B", modelId, "B", 0);
        api.createVehicle("V-D", modelId, "D", 0);
        int busy = api.createVehicle("V-B2", modelId, "B", 0);
        api.openServiceOrder(api.createTechnician("other@atlaz.com"), busy, DEPARTURE, 0);

        when().get("/vehicles/available/{userId}", technician)
                .then().statusCode(200)
                .body(arrayOf("vehicle"))
                .body("id", contains(available));
    }

    @Test
    void shouldReturnZeroConsumptionWithoutRefuelings() {
        int id = api.createVehicle("V-01", modelId, "B", 0);

        when().get("/vehicles/{id}/average-consumption", id)
                .then().statusCode(200)
                .body(schema("consumption"))
                .body(equalTo("0.0"));
    }

    @Test
    void shouldCalculateAverageConsumptionFromFinishedTrips() {
        int userId = api.createTechnician("tech@atlaz.com");
        int vehicleId = api.createVehicle("V-01", modelId, "B", 1000);
        int cityId = api.createCity("Taubate", "SP");
        int orderId = api.openServiceOrder(userId, vehicleId, DEPARTURE, 1000);
        api.finishServiceOrder(orderId, 1400, DEPARTURE.plusHours(8));
        api.createRefueling(userId, vehicleId, cityId, orderId, DEPARTURE.plusHours(2), "40", "230.00");

        when().get("/vehicles/{id}/average-consumption", vehicleId)
                .then().statusCode(200)
                .body(schema("consumption"))
                .body(equalTo("10.0"));
    }

    @Test
    void shouldReturnNotFoundForUnknownVehicle() {
        String body = """
                {"prefix": "V-01", "type": "PASSEIO", "modelId": %d, "fuelType": "ETANOL"}
                """.formatted(modelId);

        assertError(when().get("/vehicles/{id}", 999).then(), 404, NOT_FOUND);
        assertError(json(body).when().put("/vehicles/{id}", 999).then(), 404, NOT_FOUND);
        assertError(when().delete("/vehicles/{id}", 999).then(), 404, NOT_FOUND);
        assertError(json("1").when().patch("/vehicles/{id}/km-troca-oleo", 999).then(), 404, NOT_FOUND);
        assertError(when().get("/vehicles/{id}/average-consumption", 999).then(), 404, NOT_FOUND);
    }

    @Test
    void shouldRejectUnknownModel() {
        assertError(json("""
                {"prefix": "V-01", "type": "PASSEIO", "modelId": 999, "fuelType": "ETANOL"}
                """).when().post("/vehicles").then(), 404, "Model not found!");
    }

    @Test
    void shouldRejectMissingRequiredFields() {
        assertError(json("{}").when().post("/vehicles").then(), 400,
                "fuelType: must not be null; modelId: must not be null; prefix: must not be blank; "
                        + "type: must not be null");
    }

    @Test
    void shouldRejectDuplicatedPrefix() {
        api.createVehicle("V-01", modelId, "B", 0);

        assertError(json("""
                {"prefix": "V-01", "type": "PASSEIO", "modelId": %d, "fuelType": "ETANOL"}
                """.formatted(modelId)).when().post("/vehicles").then(), 409,
                "Operation violates a data integrity constraint");
    }

    @Test
    void shouldNotDeactivateVehicleWithTripInProgress() {
        int vehicleId = api.createVehicle("V-01", modelId, "B", 0);
        api.openServiceOrder(api.createTechnician("tech@atlaz.com"), vehicleId, DEPARTURE, 0);

        assertError(json("""
                        {"prefix": "V-01", "type": "PASSEIO", "modelId": %d, "fuelType": "ETANOL",
                         "status": "DESATIVADA"}
                        """.formatted(modelId)).when().put("/vehicles/{id}", vehicleId).then(), 422,
                "Não é possível inativar a viatura porque ela está vinculada a uma Ordem de Serviço em andamento.");
    }

    @Test
    void shouldReturnNotFoundWhenListingAvailableVehiclesOfUnknownUser() {
        assertError(when().get("/vehicles/available/{userId}", 999).then(), 404, "User not found!");
    }
}
