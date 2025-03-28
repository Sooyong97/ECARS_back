package com.sbb.ecars.service;

import com.sbb.ecars.dto.CallLogsDto;
import com.sbb.ecars.dto.GPTRequestDto;
import com.sbb.ecars.dto.LocationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GPTService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final LocationService locationService;

    public GPTService(LocationService locationService) {
        this.restTemplate = new RestTemplate();
        this.locationService = locationService;
    }

    public CallLogsDto processEmergencyText(GPTRequestDto request) {
        String recognizedText = request.getRecord();

        String prompt = recognizedText +
                "Analyze the emergency call transcript and provide answers in English for "
                + "'incidentType', 'incidentLocation', and 'situationDetails'. "
                + "If any information is missing, return 'X'. "
                + "Example: 'There is a fire' → incidentType: Fire, incidentLocation: X, situationDetails: X.";

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "You're a helpful assistant."),
                Map.of("role", "user", "content", prompt)
        );

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.5);
        requestBody.put("top_p", 0.5);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
        } catch (Exception e) {
            System.out.println("GPT API 호출 실패: " + e.getMessage());
            return new CallLogsDto(null, null, "X", "X", "X", "X", "X", "X", recognizedText,
                    false, "X", "X", 0.0, 0.0, "X");
        }

        // 응답 데이터 파싱 개선
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("choices")) {
            return new CallLogsDto(null, null, "X", "X", "X", "X", "X", "X", recognizedText,
                    false, "X", "X", 0.0, 0.0, "X");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (choices.isEmpty()) {
            return new CallLogsDto(null, null, "X", "X", "X", "X", "X", "X", recognizedText,
                    false, "X", "X", 0.0, 0.0, "X");
        }

        Map<String, Object> messageData = (Map<String, Object>) choices.get(0).get("message");
        String gptResponse = messageData.get("content").toString();

        // 데이터 정제
        Map<String, String> resultMap = new HashMap<>();
        String[] responseLines = gptResponse.split("\n");
        for (String line : responseLines) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                resultMap.put(parts[0].trim(), parts[1].trim());
            }
        }

        String eventLocation = resultMap.getOrDefault("incidentLocation", "X");
        LocationDto locationData = locationService.getLocationInfo(eventLocation);

        // CallLogsDto에 STT 텍스트 포함하여 반환
        return new CallLogsDto(
                null, null,
                resultMap.getOrDefault("incidentType", "X"),
                resultMap.getOrDefault("incidentLocation", "X"),
                resultMap.getOrDefault("situationDetails", "X"),
                locationData.getEstimatedAddress(),   // getOrDefault() 대신 직접 값 사용
                locationData.getEstimatedPlace(),     // getOrDefault() 대신 직접 값 사용
                locationData.getEstimatedPhone(),     // getOrDefault() 대신 직접 값 사용
                recognizedText,
                false,
                "X",
                "X",
                locationData.getLatitude(),           // getOrDefault() 대신 직접 값 사용
                locationData.getLongitude(),          // getOrDefault() 대신 직접 값 사용
                "X"
        );
    }
}