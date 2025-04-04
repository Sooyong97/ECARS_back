package com.sbb.ecars.controller;

import com.sbb.ecars.domain.Account;
import com.sbb.ecars.dto.AccountDto;
import com.sbb.ecars.service.AccountService;
import com.sbb.ecars.service.MailService;
import com.sbb.ecars.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@RestController
public class AccountController {

    static class IdRequestDto {
        private String id;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    static class EmailRequestDto {
        private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    private final AccountService accountService;
    private final MailService mailService;
    private final RedisService redisService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> register(@RequestBody AccountDto accountDto) {
        int resultCode = accountService.registerAccount(accountDto);
        Map<String, Object> result = new HashMap<>();

        if (resultCode == 1) {
            result.put("errorCode", 1);
            return ResponseEntity.status(400).body(result);
        } else if (resultCode == 0) {
            result.put("errorCode", 0);
            return ResponseEntity.status(400).body(result);
        } else {
            result.put("message", "회원가입 성공");
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/userJWT")
    public ResponseEntity<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Optional<Account> user = accountService.findAccountById(id);
        if (user.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.get().getId());
            response.put("name", user.get().getName());
            response.put("email", user.get().getEmail());
            response.put("is_admin", user.get().isAdmin());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "USER_NOT_FOUND"));
        }
    }

    // ID 중복 확인 API
    @PostMapping("/idcheck")
    public ResponseEntity<Map<String, Boolean>> checkId(@RequestBody IdRequestDto dto) {
        boolean isAvailable = accountService.isIdAvailable(dto.getId());
        Map<String, Boolean> result = new HashMap<>();
        result.put("valid", isAvailable);
        return ResponseEntity.ok(result);
    }
    // 이메일 중복 확인 API

    @PostMapping("/emailcheck")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody EmailRequestDto dto) {
        boolean isAvailable = accountService.isEmailAvailable(dto.getEmail());
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

    @PostMapping("/changepw")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String newPassword = request.get("newPassword");

        Map<String, String> response = new HashMap<>();

        if (!accountService.updatePassword(id, newPassword)) {
            response.put("message", "INVALID_PASSWORD");
            return ResponseEntity.status(400).body(response);
        }

        response.put("message", "PASSWORD_UPDATED");
        return ResponseEntity.ok(response);
    }
}
