package com.example.atlazdb.e2e;

import static com.example.atlazdb.e2e.JsonSchemas.arrayOf;
import static com.example.atlazdb.e2e.JsonSchemas.schema;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class ModelApiIT extends AbstractApiIT {

    private static final String NOT_FOUND = "Model not found";

    @Test
    void shouldCreateModel() {
        json("""
                {"nameModel": "Hilux", "nameBrand": "Toyota"}
                """)
                .when().post("/models")
                .then().statusCode(200)
                .body(schema("model"))
                .body("id", equalTo(1))
                .body("modelName", equalTo("Hilux"))
                .body("brandName", equalTo("Toyota"));
    }

    @Test
    void shouldListAndFindModels() {
        api.createModel("Hilux", "Toyota");
        int onix = api.createModel("Onix", "Chevrolet");

        when().get("/models")
                .then().statusCode(200)
                .body(arrayOf("model"))
                .body("modelName", contains("Hilux", "Onix"));

        when().get("/models/{id}", onix)
                .then().statusCode(200)
                .body(schema("model"))
                .body("brandName", equalTo("Chevrolet"));
    }

    @Test
    void shouldUpdateModel() {
        int id = api.createModel("Hilux", "Toyota");

        json("""
                {"nameModel": "SW4", "nameBrand": "Toyota"}
                """)
                .when().put("/models/{id}", id)
                .then().statusCode(200)
                .body(schema("model"))
                .body("id", equalTo(id))
                .body("modelName", equalTo("SW4"));
    }

    @Test
    void shouldDeleteModel() {
        int id = api.createModel("Hilux", "Toyota");

        when().delete("/models/{id}", id).then().statusCode(200);

        assertError(when().get("/models/{id}", id).then(), 404, NOT_FOUND);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoModels() {
        when().get("/models").then().statusCode(200).body(arrayOf("model")).body("$", empty());
    }

    @Test
    void shouldReturnNotFoundForUnknownModel() {
        assertError(when().get("/models/{id}", 999).then(), 404, NOT_FOUND);
        assertError(json("""
                {"nameModel": "SW4", "nameBrand": "Toyota"}
                """).when().put("/models/{id}", 999).then(), 404, NOT_FOUND);
        assertError(when().delete("/models/{id}", 999).then(), 404, NOT_FOUND);
    }

    @Test
    void shouldRejectInvalidModel() {
        assertError(json("{}").when().post("/models").then(), 400,
                "nameBrand: must not be blank; nameModel: must not be blank");
    }

    @Test
    void shouldNotDeleteModelInUseByVehicle() {
        int id = api.createModel("Hilux", "Toyota");
        api.createVehicle("V-01", id, "B", 0);

        assertError(when().delete("/models/{id}", id).then(), 409, "Operation violates a data integrity constraint");
    }
}
