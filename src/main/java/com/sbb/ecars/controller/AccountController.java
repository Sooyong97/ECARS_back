package com.sbb.ecars.controller;

import com.sbb.ecars.dto.AccountDto;
import com.sbb.ecars.service.AccountService;
import com.sbb.ecars.service.MailService;
import com.sbb.ecars.service.RedisService;
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
    private final MailService mailService;
    private final RedisService redisService;

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

    @PostMapping("/findid")
    public ResponseEntity<Map<String, String>> sendFindIdCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String authCode = mailService.generateAuthCode();
        redisService.saveAuthCode(email, authCode);
        mailService.sendAuthEmail(email, authCode);

        Map<String, String> response = new HashMap<>();
        response.put("message", "EMAIL_SENT");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifyid")
    public ResponseEntity<Map<String, Object>> verifyFindIdCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String storedCode = redisService.getAuthCode(email);

        Map<String, Object> result = new HashMap<>();
        if (storedCode != null && storedCode.equals(code)) {
            String id = accountService.findIdByEmail(email);
            redisService.deleteAuthCode(email);

            result.put("message", "SUCCESS");
            result.put("id", id);
            return ResponseEntity.ok(result);
        } else {
            result.put("message", "INVALID_CODE");
            return ResponseEntity.status(400).body(result);
        }
    }

    @PostMapping("/findpw")
    public ResponseEntity<Map<String, Boolean>> findPassword(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String email = request.get("email");

        Map<String, Boolean> result = new HashMap<>();
        if (accountService.validateUserByIdAndEmail(id, email)) {
            String code = mailService.generateAuthCode();
            redisService.saveAuthCode(email, code);
            mailService.sendAuthEmail(email, code);
            result.put("valid", true);
            return ResponseEntity.ok(result);
        } else {
            result.put("valid", false);
            return ResponseEntity.status(404).body(result);
        }
    }

    @PostMapping("/verifypw")
    public ResponseEntity<Map<String, String>> verifyPasswordCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String storedCode = redisService.getAuthCode(email);

        Map<String, String> response = new HashMap<>();
        if (storedCode != null && storedCode.equals(code)) {
            redisService.deleteAuthCode(email);
            response.put("message", "SUCCESS");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "INVALID_CODE");
            return ResponseEntity.status(400).body(response);
        }
    }
}
