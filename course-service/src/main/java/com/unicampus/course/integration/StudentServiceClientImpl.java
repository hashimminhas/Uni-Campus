package com.unicampus.course.integration;

import com.unicampus.course.exception.DependencyException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class StudentServiceClientImpl implements StudentServiceClient {

    private final RestClient restClient;

    @Value("${student.service.base-url}")
    private String studentServiceBaseUrl;

    @Override
    public boolean isStudentEligible(String studentId) {
        try {
            StudentValidationResponse response = restClient.get()
                    .uri(studentServiceBaseUrl + "/students/validate/{studentId}", studentId)
                    .retrieve()
                    .body(StudentValidationResponse.class);

            return true;
        } catch (RestClientException ex) {
            throw new DependencyException("Student Service is unavailable");
        }
    }
}
