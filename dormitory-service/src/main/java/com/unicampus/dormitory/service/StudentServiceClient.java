package com.unicampus.dormitory.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class StudentServiceClient {

    private final RestTemplate restTemplate;
    private final String studentServiceUrl;

    public StudentServiceClient(RestTemplate restTemplate, @Value("${student-service.url}") String studentServiceUrl) {
        this.restTemplate = restTemplate;
        this.studentServiceUrl = studentServiceUrl;
    }

    public boolean isStudentActive(UUID studentId) {
        try {
            String url = studentServiceUrl + "/students/validate/" + studentId;
            ResponseEntity<com.unicampus.dormitory.dto.ValidateStudentResponse> response = restTemplate.getForEntity(url, com.unicampus.dormitory.dto.ValidateStudentResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().isValid();
            }
            return false;
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.Forbidden e) {
            return false;
        } catch (Exception e) {
            // Depending on requirements, we could throw an exception or return false
            return false;
        }
    }
}
