package com.example.atlazdb.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

class CorsConfigTest {

    @Test
    void shouldAllowFrontEndOriginsAndAllUsedHttpMethods() {
        InspectableCorsRegistry registry = new InspectableCorsRegistry();

        new CorsConfig().addCorsMappings(registry);

        CorsConfiguration configuration = registry.configurations().get("/**");
        assertThat(configuration.getAllowedOrigins())
                .containsExactly("http://localhost:5173", "https://atlaz-front-end.vercel.app");
        assertThat(configuration.getAllowedMethods()).containsExactly("GET", "POST", "PUT", "PATCH", "DELETE");
    }

    private static final class InspectableCorsRegistry extends CorsRegistry {
        Map<String, CorsConfiguration> configurations() {
            return getCorsConfigurations();
        }
    }
}
