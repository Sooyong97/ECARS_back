package com.sbb.ecars.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private String estimatedAddress; // 추정 주소
    private String estimatedPlace;   // 추정 장소
    private String estimatedPhone;   // 추정 전화번호
    private double latitude;         // 위도
    private double longitude;        // 경도
}
