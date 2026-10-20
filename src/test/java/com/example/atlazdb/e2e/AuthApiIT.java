package com.example.atlazdb.e2e;

import static com.example.atlazdb.e2e.ApiClient.USER_SECRET;
import static com.example.atlazdb.e2e.JsonSchemas.schema;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.restassured.response.Response;

class AuthApiIT extends AbstractApiIT {

    private static final String INVALID_CREDENTIALS = "Credenciais inválidas";

    @Test
    void shouldLoginAdministratorWithValidCredentials() {
        int id = api.createUser("admin@atlaz.com", "ADMIN", "");

        login("admin@atlaz.com", USER_SECRET)
                .then().statusCode(200)
                .body(schema("login"))
                .body("id", equalTo(id))
                .body("profile", equalTo("ADMIN"))
                .body("name", equalTo("User admin@atlaz.com"));
    }

    @Test
    void shouldLoginTechnicianWithTechnicianProfile() {
        int id = api.createTechnician("tech@atlaz.com");

        login("tech@atlaz.com", USER_SECRET)
                .then().statusCode(200)
                .body(schema("login"))
                .body("id", equalTo(id))
                .body("profile", equalTo("TECNICO"));
    }

    @Test
    void shouldRejectWrongPassword() {
        api.createTechnician("tech@atlaz.com");

        assertError(login("tech@atlaz.com", "wrong-value").then(), 401, INVALID_CREDENTIALS);
    }

    @Test
    void shouldRejectUnknownEmail() {
        assertError(login("nobody@atlaz.com", USER_SECRET).then(), 401, INVALID_CREDENTIALS);
    }

    @Test
    void shouldRejectMissingCredentials() {
        assertError(json("{}").when().post("/auth/login").then(), 400,
                "email: must not be blank; password: must not be blank");
    }

    @Test
    void shouldRejectMalformedBody() {
        assertError(json("not json").when().post("/auth/login").then(), 400, "Invalid request parameters or body");
    }

    private static Response login(String email, String secret) {
        return json("""
                {"email": "%s", "password": "%s"}
                """.formatted(email, secret))
                .when().post("/auth/login");
    }
}
