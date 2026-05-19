package com.unicampus.mealplan.service;

import com.unicampus.mealplan.dto.ValidateStudentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class StudentServiceClient {

    private final RestTemplate restTemplate;
    private final String studentServiceUrl;

    public StudentServiceClient(RestTemplate restTemplate, @Value("${STUDENT_SERVICE_URL:http://localhost:8081}") String studentServiceUrl) {
        this.restTemplate = restTemplate;
        this.studentServiceUrl = studentServiceUrl;
    }

    public boolean isStudentActive(UUID studentId) {
        try {
            String url = studentServiceUrl + "/students/validate/" + studentId;
            ResponseEntity<ValidateStudentResponse> response = restTemplate.getForEntity(url, ValidateStudentResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().isValid();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
