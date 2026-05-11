package com.unicampus.notification.client;

import com.unicampus.notification.exception.StudentNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class StudentServiceClient {

    private final WebClient webClient;

    public StudentServiceClient(@Value("${student.service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public StudentResponse getStudent(UUID studentId) {
        return webClient.get()
                .uri("/students/{id}", studentId)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new StudentNotFoundException(studentId))
                )
                .bodyToMono(StudentResponse.class)
                .block();
    }
}
