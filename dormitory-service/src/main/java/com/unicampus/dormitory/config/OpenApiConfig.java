package com.unicampus.dormitory.config;

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
                        .title("Dormitory Service API")
                        .description("UniCampus Dormitory Service – manages room inventory and semester-based student assignments.")
                        .version("v1.0.0"));
    }
}
