package com.sbb.ecars.controller;

import com.sbb.ecars.dto.AccountDto;
import com.sbb.ecars.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody AccountDto accountDto) {
        String response = accountService.registerAccount(accountDto);
        return ResponseEntity.ok(response);
    }

    // ID 중복 확인 API
    @PostMapping("/idcheck")
    public ResponseEntity<Boolean> checkId(@RequestBody String id) {
        return ResponseEntity.ok(accountService.isIdAvailable(id));
    }

    // 이메일 중복 확인 API
    @PostMapping("/emailcheck")
    public ResponseEntity<Boolean> checkEmail(@RequestBody String email) {
        return ResponseEntity.ok(accountService.isEmailAvailable(email));
    }

    // 이메일로 ID 찾기
    @PostMapping("/findid")
    public ResponseEntity<String> findId(@RequestBody String email) {
        return ResponseEntity.ok(accountService.findIdByEmail(email));
    }
}
