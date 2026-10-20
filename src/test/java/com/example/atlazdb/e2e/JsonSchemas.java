package com.example.atlazdb.e2e;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.hamcrest.Matcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.module.jsv.JsonSchemaValidator;

/**
 * JSON Schema matchers for the API responses (schemas are stored in src/test/resources/schemas).
 */
final class JsonSchemas {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonSchemas() {
    }

    static JsonSchemaValidator schema(String name) {
        return matchesJsonSchemaInClasspath("schemas/" + name + ".json");
    }

    /**
     * Builds the schema of a JSON array whose items must all match the given schema.
     */
    static Matcher<?> arrayOf(String name) {
        try (InputStream input = JsonSchemas.class.getResourceAsStream("/schemas/" + name + ".json")) {
            ObjectNode item = (ObjectNode) MAPPER.readTree(input);
            item.remove("$schema");
            ObjectNode array = MAPPER.createObjectNode();
            array.put("$schema", "http://json-schema.org/draft-04/schema#");
            array.put("type", "array");
            // Internal references (#/definitions/...) are resolved from the root schema
            if (item.has("definitions")) {
                array.set("definitions", item.remove("definitions"));
            }
            array.set("items", item);
            return matchesJsonSchema(MAPPER.writeValueAsString(array));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
