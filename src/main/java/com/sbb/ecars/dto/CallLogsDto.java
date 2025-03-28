package com.sbb.ecars.dto;

import com.sbb.ecars.domain.CallLogs;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallLogsDto {
    private Long id;
    private LocalDateTime date;
    private String category;
    private String location;
    private String details;
    private String addressName;
    private String placeName;
    private String phoneNumber;
    private String fullText;
    private boolean isDuplicate;
    private String emergencyType;
    private String audioFile;
    private Double lat;
    private Double lng;
    private String jurisdiction;

    public static CallLogsDto fromEntity(CallLogs callLogs) {
        return CallLogsDto.builder()
                .id(callLogs.getId())
                .date(callLogs.getDate())
                .category(callLogs.getCategory())
                .location(callLogs.getLocation())
                .details(callLogs.getDetails())
                .addressName(callLogs.getAddressName())
                .placeName(callLogs.getPlaceName())
                .phoneNumber(callLogs.getPhoneNumber())
                .fullText(callLogs.getFullText())
                .isDuplicate(callLogs.isDuplicate())
                .emergencyType(callLogs.getEmergencyType().name())
                .audioFile(callLogs.getAudioFile())
                .lat(callLogs.getLat())
                .lng(callLogs.getLng())
                .jurisdiction(callLogs.getJurisdiction())
                .build();
    }
}
