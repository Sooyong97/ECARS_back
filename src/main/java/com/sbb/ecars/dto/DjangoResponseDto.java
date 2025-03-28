package com.sbb.ecars.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DjangoResponseDto {
    private String category;       // 사건 분류
    private String emergencyType;  // 긴급 여부
    private String jurisdiction;   // 관할서
}