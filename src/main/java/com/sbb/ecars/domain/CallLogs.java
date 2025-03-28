package com.sbb.ecars.domain;

import com.sbb.ecars.dto.CallLogsDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "call_logs")
public class CallLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date = LocalDateTime.now(); // 신고 날짜 (자동 생성)
    private String category;       // 사건 분류 (GPT + Django 분석 결과)
    private String location;       // 사건 발생 장소 (GPT 분석 결과)
    private String details;        // 구체적인 현장 상태 (GPT 분석 결과)
    private String addressName;    // 추정 주소 (Location 서비스 결과)
    private String placeName;      // 추정 장소 (Location 서비스 결과)
    private String phoneNumber;    // 추정 번호 (Location 서비스 결과)
    private String fullText;       // STT 변환된 텍스트
    private boolean isDuplicate;   // 중복 신고 여부 (1시간 내 동일 사건 분류 & 장소)

    @Enumerated(EnumType.STRING)
    private EmergencyType emergencyType;  // 긴급 여부 (Django 분석 결과)

    private String audioFile;      // 음성 파일 경로
    private Double lat;            // 위도 (Location 서비스 결과)
    private Double lng;            // 경도 (Location 서비스 결과)
    private String jurisdiction;   // 관할서 (Django 분석 결과 또는 자동 설정)

    public CallLogs(CallLogsDto dto) {
        this.category = dto.getCategory();
        this.location = dto.getLocation();
        this.details = dto.getDetails();
        this.addressName = dto.getAddressName();
        this.placeName = dto.getPlaceName();
        this.phoneNumber = dto.getPhoneNumber();
        this.fullText = dto.getFullText();
        this.isDuplicate = dto.isDuplicate();
        this.emergencyType = EmergencyType.valueOf(dto.getEmergencyType().toUpperCase());
        this.audioFile = dto.getAudioFile();
        this.lat = dto.getLat();
        this.lng = dto.getLng();
        this.jurisdiction = dto.getJurisdiction();
    }

    // 신고 저장 또는 수정 시 관할서 자동 설정
    @PrePersist
    @PreUpdate
    public void setJurisdictionAutomatically() {
        if (this.category == null) {
            this.jurisdiction = "미확인";
            return;
        }

        switch (this.category) {
            case "질병(중증 외)":
            case "부상":
            case "질병(중증)":
            case "심정지":
            case "임산부":
            case "기타구급":
            case "기타구조":
                this.jurisdiction = "구급상황관리센터";
                break;
            case "사고":
            case "약물중독":
            case "안전사고":
            case "대물사고":
            case "자살":
                this.jurisdiction = "경찰청, 구급상황관리센터";
                break;
            case "일반화재":
            case "기타화재":
            case "산불":
                this.jurisdiction = "안전신고센터";
                break;
            case "기타":
                this.jurisdiction = "민원센터";
                break;
            default:
                this.jurisdiction = "미확인";
                break;
        }
    }

    public enum EmergencyType {
        EMERGENCY, NON_EMERGENCY
    }
}