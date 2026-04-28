package com.unicampus.mealplan.config;

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
                        .title("Meal Plan Service API")
                        .description("UniCampus Meal Plan Service – manages meal plan catalog and student subscriptions per semester.")
                        .version("v1.0.0"));
    }
}
