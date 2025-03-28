package com.sbb.ecars.controller;

import com.sbb.ecars.dto.AccountDto;
import com.sbb.ecars.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> register(@RequestBody AccountDto accountDto) {
        String response = accountService.registerAccount(accountDto);
        Map<String, String> result = new HashMap<>();
        result.put("message", response);
        return ResponseEntity.ok(result);
    }

    // ID 중복 확인 API
    @PostMapping("/idcheck")
    public ResponseEntity<Map<String, Boolean>> checkId(@RequestBody String id) {
        boolean isAvailable = accountService.isIdAvailable(id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("valid", isAvailable);
        return ResponseEntity.ok(result);
    }

    // 이메일 중복 확인 API
    @PostMapping("/emailcheck")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody String email) {
        boolean isAvailable = accountService.isEmailAvailable(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("valid", isAvailable);
        return ResponseEntity.ok(result);
    }

    // 이메일로 ID 찾기
    @PostMapping("/findid")
    public ResponseEntity<String> findId(@RequestBody String email) {
        return ResponseEntity.ok(accountService.findIdByEmail(email));
    }
}
