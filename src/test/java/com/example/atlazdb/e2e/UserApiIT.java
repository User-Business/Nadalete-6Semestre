package com.example.atlazdb.e2e;

import static com.example.atlazdb.e2e.JsonSchemas.arrayOf;
import static com.example.atlazdb.e2e.JsonSchemas.schema;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class UserApiIT extends AbstractApiIT {

    private static final String NOT_FOUND = "User not found";

    @Test
    void shouldCreateUserAsAvailableWithoutExposingPassword() {
        json(userBody("Mary", "mary@atlaz.com", "TECNICO", null))
                .when().post("/users")
                .then().statusCode(200)
                .body(schema("user"))
                .body("id", equalTo(1))
                .body("name", equalTo("Mary"))
                .body("registrationNumber", equalTo("REG-mary@atlaz.com"))
                .body("email", equalTo("mary@atlaz.com"))
                .body("profile", equalTo("TECNICO"))
                .body("userStatus", equalTo("DISPONIVEL"))
                .body("cnhTypes", containsInAnyOrder("B", "C"));
    }

    @Test
    void shouldCreateUserWithInformedStatus() {
        json(userBody("Mary", "mary@atlaz.com", "ADMIN", "EM_CAMPO"))
                .when().post("/users")
                .then().statusCode(200)
                .body(schema("user"))
                .body("userStatus", equalTo("EM_CAMPO"));
    }

    @Test
    void shouldListAndFindUsers() {
        api.createTechnician("john@atlaz.com");
        int mary = api.createTechnician("mary@atlaz.com");

        when().get("/users")
                .then().statusCode(200)
                .body(arrayOf("user"))
                .body("email", contains("john@atlaz.com", "mary@atlaz.com"));

        when().get("/users/{id}", mary)
                .then().statusCode(200)
                .body(schema("user"))
                .body("email", equalTo("mary@atlaz.com"));
    }

    @Test
    void shouldUpdateUserKeepingStatusWhenNotInformed() {
        int id = api.createTechnician("mary@atlaz.com");

        json(userBody("Mary Jane", "mary@atlaz.com", "ADMIN", null))
                .when().put("/users/{id}", id)
                .then().statusCode(200)
                .body(schema("user"))
                .body("name", equalTo("Mary Jane"))
                .body("profile", equalTo("ADMIN"))
                .body("userStatus", equalTo("DISPONIVEL"));
    }

    @Test
    void shouldDeactivateUserWithoutTripInProgress() {
        int id = api.createTechnician("mary@atlaz.com");

        json(userBody("Mary", "mary@atlaz.com", "TECNICO", "DESLIGADO"))
                .when().put("/users/{id}", id)
                .then().statusCode(200)
                .body(schema("user"))
                .body("userStatus", equalTo("DESLIGADO"));
    }

    @Test
    void shouldDeleteUser() {
        int id = api.createTechnician("mary@atlaz.com");

        when().delete("/users/{id}", id).then().statusCode(200);

        assertError(when().get("/users/{id}", id).then(), 404, NOT_FOUND);
    }

    @Test
    void shouldListUserStatuses() {
        when().get("/users/user-status")
                .then().statusCode(200)
                .body(schema("string-list"))
                .body("$", contains("DISPONIVEL", "EM_CAMPO", "DESLIGADO"));
    }

    @Test
    void shouldReturnNotFoundForUnknownUser() {
        assertError(when().get("/users/{id}", 999).then(), 404, NOT_FOUND);
        assertError(json(userBody("Mary", "mary@atlaz.com", "ADMIN", null))
                .when().put("/users/{id}", 999).then(), 404, NOT_FOUND);
        assertError(when().delete("/users/{id}", 999).then(), 404, NOT_FOUND);
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertError(json(userBody("Mary", "not-an-email", "ADMIN", null)).when().post("/users").then(), 400,
                "email: must be a well-formed email address");
    }

    @Test
    void shouldRejectUnknownProfile() {
        assertError(json(userBody("Mary", "mary@atlaz.com", "BOSS", null)).when().post("/users").then(), 400,
                "Invalid request parameters or body");
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        api.createTechnician("mary@atlaz.com");

        assertError(json(userBody("Other Mary", "mary@atlaz.com", "ADMIN", null)).when().post("/users").then(), 409,
                "Operation violates a data integrity constraint");
    }

    @Test
    void shouldNotDeactivateUserWithTripInProgress() {
        int userId = api.createTechnician("mary@atlaz.com");
        int vehicleId = api.createVehicle("V-01", api.createModel("Hilux", "Toyota"), "B", 0);
        api.openServiceOrder(userId, vehicleId, LocalDateTime.of(2026, 4, 2, 8, 0), 0);

        assertError(json(userBody("Mary", "mary@atlaz.com", "TECNICO", "DESLIGADO"))
                        .when().put("/users/{id}", userId).then(), 422,
                "Não é possível inativar o técnico porque ele está vinculado a uma Ordem de Serviço em andamento.");
    }

    private static String userBody(String name, String email, String profile, String status) {
        return """
                {"name": "%s", "registrationNumber": "REG-%s", "email": "%s", "passwordHash": "%s",
                 "profile": "%s", "userStatus": %s, "cnhTypes": ["B", "C"]}
                """.formatted(name, email, email, ApiClient.USER_SECRET, profile,
                status == null ? null : "\"" + status + "\"");
    }
}
