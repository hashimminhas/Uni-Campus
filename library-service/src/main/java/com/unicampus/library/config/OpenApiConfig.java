package com.unicampus.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Service API")
                        .description("UniCampus Library Service – manages book catalog, borrowing lifecycle, and overdue tracking.")
                        .version("v1.0.0"));
    }
}
