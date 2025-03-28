package com.sbb.ecars.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GPTResponseDto {
    private String incidentType;       // 사건 분류
    private String incidentLocation;   // 사건 발생 장소
    private String situationDetails;   // 구체적인 현장 상태
    private String estimatedAddress;   // 추정 주소
    private String estimatedPlace;     // 추정 장소
    private String estimatedPhone;     // 추정 번호
    private String latitude;           // 위도
    private String longitude;          // 경도
}
