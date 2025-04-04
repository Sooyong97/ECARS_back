package com.sbb.ecars.controller;

import com.sbb.ecars.domain.CallLogs;
import com.sbb.ecars.dto.CallLogsDto;
import com.sbb.ecars.repository.CallLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calllogs")
@RequiredArgsConstructor
public class CallLogsController {

    private final CallLogsRepository callLogsRepository;

    // 전체 신고 목록
    @GetMapping
    public ResponseEntity<List<CallLogsDto>> getAllLogs() {
        List<CallLogsDto> result = callLogsRepository.findAll().stream()
                .map(CallLogsDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 특정 신고 조회
    @GetMapping("/{id}")
    public ResponseEntity<CallLogsDto> getLogById(@PathVariable Long id) {
        CallLogs log = callLogsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        return ResponseEntity.ok(CallLogsDto.fromEntity(log));
    }

    // 중복 여부 필터링된 목록
    @GetMapping("/duplicates")
    public ResponseEntity<List<CallLogsDto>> getDuplicates(@RequestParam boolean isDuplicate) {
        List<CallLogsDto> result = callLogsRepository.findByIsDuplicate(isDuplicate).stream()
                .map(CallLogsDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 일별 신고 현황
    @GetMapping("/daystats")
    public ResponseEntity<List<Map<String, Object>>> getDayStats() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> grouped = callLogsRepository.findAll().stream()
                .filter(log -> !log.isDuplicate())
                .collect(Collectors.groupingBy(
                        log -> log.getDate().toLocalDate().format(formatter),
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("annotated_date", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("annotated_date")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 출동통계 (카테고리별 신고 수)
    @GetMapping("/categorycount")
    public ResponseEntity<List<Map<String, Object>>> getCategoryStats() {
        Map<String, Long> grouped = callLogsRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        CallLogs::getCategory,
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("category", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("category")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
