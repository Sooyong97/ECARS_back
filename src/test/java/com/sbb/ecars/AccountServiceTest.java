package com.sbb.ecars;

import com.sbb.ecars.domain.Account;
import com.sbb.ecars.dto.AccountDto;
import com.sbb.ecars.repository.AccountRepository;
import com.sbb.ecars.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private Account testAccount;
    private AccountDto testAccountDto;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id("testUser")
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .isAdmin(false)
                .build();

        testAccountDto = AccountDto.builder()
                .id("testUser")
                .name("Test User")
                .email("test@example.com")
                .password("Password123!")
                .build();
    }

    @Test
    void registerAccount_Success() {
        when(accountRepository.findByEmail(testAccountDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testAccountDto.getPassword())).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        String result = accountService.registerAccount(testAccountDto);

        assertEquals("회원가입 성공", result);
    }

    @Test
    void registerAccount_EmailAlreadyExists() {
        when(accountRepository.findByEmail(testAccountDto.getEmail())).thenReturn(Optional.of(testAccount));

        String result = accountService.registerAccount(testAccountDto);

        assertEquals("이미 존재하는 이메일입니다.", result);
    }

    @Test
    void registerAccount_InvalidPassword() {
        AccountDto invalidPasswordDto = AccountDto.builder()
                .id("testUser2")
                .name("Invalid User")
                .email("invalid@example.com")
                .password("weakpass") // 규칙을 따르지 않는 비밀번호 (예: 대문자, 숫자, 특수문자 없음)
                .build();

        String result = accountService.registerAccount(invalidPasswordDto);

        assertEquals("비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.", result);
    }

    @Test
    void isIdAvailable_ReturnsTrue() {
        when(accountRepository.existsById("newUser")).thenReturn(false);
        assertTrue(accountService.isIdAvailable("newUser"));
    }

    @Test
    void isIdAvailable_ReturnsFalse() {
        when(accountRepository.existsById("testUser")).thenReturn(true);
        assertFalse(accountService.isIdAvailable("testUser"));
    }

    @Test
    void findIdByEmail_ReturnsId() {
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testAccount));
        assertEquals("testUser", accountService.findIdByEmail("test@example.com"));
    }

    @Test
    void findIdByEmail_NotFound() {
        when(accountRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertEquals("가입된 아이디가 없습니다.", accountService.findIdByEmail("notfound@example.com"));
    }
}
