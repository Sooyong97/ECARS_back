package com.sbb.ecars.controller;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/text")
public class TextAnalysisController {

    private final RestTemplate restTemplate;

    public TextAnalysisController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostMapping("/predict")
    public ResponseEntity<?> predictText(@RequestBody Map<String, String> request) {
        String text = request.get("full_text");
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No text provided"));
        }

        String djangoApiUrl = "http://localhost:8000/text/predict/";

        ResponseEntity<Map> response = restTemplate.postForEntity(djangoApiUrl, request, Map.class);
        return ResponseEntity.ok(response.getBody());
    }
}
