package com.sbb.ecars.controller;

import com.sbb.ecars.dto.AuthRequestDto;
import com.sbb.ecars.service.AuthService;
import com.sbb.ecars.service.MailService;
import com.sbb.ecars.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MailService mailService;
    private final RedisService redisService;

    // JWT 로그인
    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequestDto request) {
        String token = authService.authenticate(request.getId(), request.getPassword());
        if (token.equals("INVALID_CREDENTIALS")) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "INVALID_CREDENTIALS");
            return ResponseEntity.status(401).body(error);
        }

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    // 이메일 인증 코드 전송
    @PostMapping("/send-email")
    public ResponseEntity<Map<String, String>> sendAuthEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String authCode = mailService.generateAuthCode();
        redisService.saveAuthCode(email, authCode);
        mailService.sendAuthEmail(email, authCode);

        Map<String, String> response = new HashMap<>();
        response.put("message", "EMAIL_SENT");
        return ResponseEntity.ok(response);
    }

    // 인증 코드 확인
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyAuthCode(@RequestBody Map<String, String> request) {
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
