package com.example.atlazdb.e2e;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.math.BigDecimal;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

/**
 * Base class of the system tests: the whole application runs on a random port, backed by a real
 * PostgreSQL database isolated in a Testcontainer (schema created by the Flyway migrations).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractApiIT {

    protected static final String ERROR_SCHEMA = "schemas/error.json";

    // Singleton container shared by every system test class (and by the cached Spring context)
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected ApiClient api;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void setUpApiAndCleanDatabase() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        RestAssured.config = RestAssuredConfig.config()
                .jsonConfig(JsonConfig.jsonConfig().numberReturnType(NumberReturnType.BIG_DECIMAL));
        jdbcTemplate.execute("TRUNCATE abastecimento, ordem_servico, usuario_cnh, viatura, usuario, modelo, cidade "
                + "RESTART IDENTITY CASCADE");
        api = new ApiClient();
    }

    protected static RequestSpecification json() {
        return given().contentType(ContentType.JSON).accept(ContentType.JSON);
    }

    protected static RequestSpecification json(String body) {
        return json().body(body);
    }

    /**
     * Matches a JSON number by value, ignoring its representation (integer, decimal or scale).
     */
    protected static Matcher<Number> numericValue(String expected) {
        BigDecimal expectedValue = new BigDecimal(expected);
        return new CustomTypeSafeMatcher<>("a number equal to " + expected) {
            @Override
            protected boolean matchesSafely(Number actual) {
                return new BigDecimal(actual.toString()).compareTo(expectedValue) == 0;
            }
        };
    }

    protected static ValidatableResponse assertError(ValidatableResponse response, int status, String message) {
        return response.statusCode(status)
                .body(matchesJsonSchemaInClasspath(ERROR_SCHEMA))
                .body("status", equalTo(status))
                .body("message", equalTo(message));
    }
}
