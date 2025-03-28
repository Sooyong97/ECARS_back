package com.sbb.ecars.service;

import com.sbb.ecars.dto.LocationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class LocationService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public LocationDto getLocationInfo(String location) {
        // Kakao API URL 설정
        String url = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query", location)
                .queryParam("page", 1)
                .build()
                .toUriString();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 호출 및 예외 처리
        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            return new LocationDto("X", "X", "X", 0.0, 0.0);
        }

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            return new LocationDto("X", "X", "X", 0.0, 0.0);
        }

        List<Map<String, Object>> places = (List<Map<String, Object>>) response.getBody().get("documents");

        if (places == null || places.isEmpty()) {
            return new LocationDto("X", "X", "X", 0.0, 0.0);
        }

        Map<String, Object> address = places.get(0);

        return new LocationDto(
                (String) address.getOrDefault("address_name", "X"),
                (String) address.getOrDefault("place_name", "X"),
                (String) address.getOrDefault("phone", "X"),
                Double.parseDouble((String) address.getOrDefault("y", "0.0")),
                Double.parseDouble((String) address.getOrDefault("x", "0.0"))
        );
    }
}