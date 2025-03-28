package com.sbb.ecars.service;

import com.sbb.ecars.dto.DjangoResponseDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DjangoService {

    private final RestTemplate restTemplate;

    public DjangoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DjangoResponseDto sendFullTextToDjango(String fullText) {
        String djangoUrl = "http://127.0.0.1:8000/text/predict/";

        // 요청 데이터 설정
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("full_text", fullText);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    djangoUrl, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return new DjangoResponseDto(
                        (String) responseBody.getOrDefault("prediction", "X"),  // category
                        (String) responseBody.getOrDefault("prediction2", "X"), // emergency_type
                        (String) responseBody.getOrDefault("jurisdiction", "X")
                );
            }
        } catch (Exception e) {
            System.out.println("Django API 호출 실패: " + e.getMessage());
        }

        return new DjangoResponseDto("X", "X", "X"); // 기본값 반환
    }
}