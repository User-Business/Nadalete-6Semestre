package com.example.atlazdb.e2e;

import static com.example.atlazdb.e2e.JsonSchemas.arrayOf;
import static com.example.atlazdb.e2e.JsonSchemas.schema;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class CityApiIT extends AbstractApiIT {

    @Test
    void shouldCreateCity() {
        json("""
                {"name": "Sao Jose dos Campos", "uf": "SP"}
                """)
                .when().post("/cities")
                .then().statusCode(200)
                .body(schema("city"))
                .body("id", equalTo(1))
                .body("name", equalTo("Sao Jose dos Campos"))
                .body("uf", equalTo("SP"));
    }

    @Test
    void shouldListAndFindCities() {
        int taubate = api.createCity("Taubate", "SP");
        api.createCity("Parati", "RJ");

        when().get("/cities")
                .then().statusCode(200)
                .body(arrayOf("city"))
                .body("name", contains("Taubate", "Parati"))
                .body("uf", contains("SP", "RJ"));

        when().get("/cities/{id}", taubate)
                .then().statusCode(200)
                .body(schema("city"))
                .body("name", equalTo("Taubate"));
    }

    @Test
    void shouldUpdateCity() {
        int id = api.createCity("Taubate", "SP");

        json("""
                {"name": "Jacarei", "uf": "SP"}
                """)
                .when().put("/cities/{id}", id)
                .then().statusCode(200)
                .body(schema("city"))
                .body("id", equalTo(id))
                .body("name", equalTo("Jacarei"));
    }

    @Test
    void shouldDeleteCity() {
        int id = api.createCity("Taubate", "SP");

        when().delete("/cities/{id}", id).then().statusCode(200);

        assertError(when().get("/cities/{id}", id).then(), 404, "City not found");
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoCities() {
        when().get("/cities")
                .then().statusCode(200)
                .body(arrayOf("city"))
                .body("$", empty());
    }

    @Test
    void shouldReturnNotFoundForUnknownCity() {
        assertError(when().get("/cities/{id}", 999).then(), 404, "City not found");
        assertError(json("""
                {"name": "Jacarei", "uf": "SP"}
                """).when().put("/cities/{id}", 999).then(), 404, "City not found");
        assertError(when().delete("/cities/{id}", 999).then(), 404, "City not found");
    }

    @Test
    void shouldRejectInvalidCity() {
        assertError(json("""
                {"name": " ", "uf": "SPX"}
                """).when().post("/cities").then(), 400,
                "name: must not be blank; uf: size must be between 2 and 2");
    }

    @Test
    void shouldRejectMalformedJson() {
        assertError(json("{\"name\": ").when().post("/cities").then(), 400, "Invalid request parameters or body");
    }

    @Test
    void shouldRejectNonNumericId() {
        assertError(when().get("/cities/{id}", "abc").then(), 400, "Invalid request parameters or body");
    }

    @Test
    void shouldNotDeleteCityReferencedByRefueling() {
        int cityId = api.createCity("Taubate", "SP");
        int userId = api.createTechnician("tech@atlaz.com");
        int vehicleId = api.createVehicle("V-01", api.createModel("Hilux", "Toyota"), "B", 0);
        api.createRefueling(userId, vehicleId, cityId, null, LocalDateTime.of(2026, 4, 2, 10, 0), "40", "250.00");

        assertError(given().when().delete("/cities/{id}", cityId).then(), 409,
                "Operation violates a data integrity constraint");
    }
}
