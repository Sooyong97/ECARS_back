package com.sbb.ecars;

import com.sbb.ecars.config.JwtConfig;
import com.sbb.ecars.domain.Account;
import com.sbb.ecars.repository.AccountRepository;
import com.sbb.ecars.service.AuthService;
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
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtConfig jwtConfig;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id("test")
                .name("testUser")
                .email("test@test.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void authenticate_Success() {
        when(accountRepository.findById("test")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("password123", testAccount.getPassword())).thenReturn(true);
        when(jwtConfig.getSecret()).thenReturn("testSecrettestSecrettestSecrettestSecrettestSecrettestSecret");
        when(jwtConfig.getExpiration()).thenReturn(3600000L);

        String token = authService.authenticate("test", "password123");
        assertNotNull(token);
        assertNotEquals("INVALID_CREDENTIALS", token);
    }

    @Test
    void authenticate_InvalidPassword() {
        when(accountRepository.findById("test")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("wrongPassword", testAccount.getPassword())).thenReturn(false);

        String result = authService.authenticate("test", "wrongPassword");
        assertEquals("INVALID_CREDENTIALS", result);
    }

    @Test
    void authenticate_UserNotFound() {
        when(accountRepository.findById("unknownUser")).thenReturn(Optional.empty());

        String result = authService.authenticate("unknownUser", "password123");
        assertEquals("INVALID_CREDENTIALS", result);
    }
}