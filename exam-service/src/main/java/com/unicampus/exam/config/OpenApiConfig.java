package com.unicampus.exam.config;

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
                        .title("Exam Service API")
                        .description("UniCampus Exam Service – manages exam timetables linked to courses and enrolled students.")
                        .version("v1.0.0"));
    }
}
