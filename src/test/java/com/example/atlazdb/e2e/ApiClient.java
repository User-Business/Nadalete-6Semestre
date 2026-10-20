package com.example.atlazdb.e2e;

import static io.restassured.RestAssured.given;

import java.time.LocalDateTime;

import io.restassured.http.ContentType;

/**
 * Creates the data needed by the system tests through the public REST API itself.
 */
final class ApiClient {

    static final String USER_SECRET = "s3cr3t-value";

    int createCity(String name, String uf) {
        return post("/cities", """
                {"name": "%s", "uf": "%s"}
                """.formatted(name, uf));
    }

    int createModel(String modelName, String brandName) {
        return post("/models", """
                {"nameModel": "%s", "nameBrand": "%s"}
                """.formatted(modelName, brandName));
    }

    int createUser(String email, String profile, String licenses) {
        return post("/users", """
                {"name": "User %1$s", "registrationNumber": "%1$s", "email": "%1$s",
                 "passwordHash": "%2$s", "profile": "%3$s", "cnhTypes": [%4$s]}
                """.formatted(email, USER_SECRET, profile, licenses));
    }

    int createTechnician(String email) {
        return createUser(email, "TECNICO", "\"B\"");
    }

    int createVehicle(String prefix, int modelId, String requiredLicense, double km) {
        return post("/vehicles", """
                {"prefix": "%s", "type": "UTILITARIO", "modelId": %d, "fuelType": "FLEX",
                 "tipoCnhNecessaria": "%s", "km": %s}
                """.formatted(prefix, modelId, requiredLicense, km));
    }

    int openServiceOrder(int userId, int vehicleId, LocalDateTime departureDate, int departureKm) {
        return post("/service-orders", """
                {"serviceType": "INSPECAO", "destinationLocation": "Taubate", "justification": "Routine",
                 "requester": "Manager", "departureKm": %d, "departureDate": "%s",
                 "userId": %d, "vehicleId": %d}
                """.formatted(departureKm, departureDate, userId, vehicleId));
    }

    void finishServiceOrder(int serviceOrderId, int arrivalKm, LocalDateTime returnDate) {
        given().contentType(ContentType.JSON)
                .body("""
                        {"arrivalKm": %d, "returnDate": "%s"}
                        """.formatted(arrivalKm, returnDate))
                .put("/service-orders/{id}", serviceOrderId)
                .then().statusCode(200);
    }

    int createRefueling(int userId, int vehicleId, int cityId, Integer serviceOrderId, LocalDateTime dateTime,
                        String liters, String totalValue) {
        return post("/refuelings", """
                {"dateTime": "%s", "liters": %s, "totalValue": %s, "receiptNumber": "NF-%s",
                 "userId": %d, "vehicleId": %d, "cityId": %d, "serviceOrderId": %s}
                """.formatted(dateTime, liters, totalValue, liters, userId, vehicleId, cityId, serviceOrderId));
    }

    private static int post(String path, String body) {
        return given().contentType(ContentType.JSON)
                .body(body)
                .post(path)
                .then().statusCode(200)
                .extract().path("id");
    }
}
