package com.unicampus.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${student-service.url}")
    private String studentServiceUrl;

    @Bean
    public WebClient studentWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(studentServiceUrl)
                .build();
    }
}
