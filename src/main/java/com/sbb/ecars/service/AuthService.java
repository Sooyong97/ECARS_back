package com.sbb.ecars.service;

import com.sbb.ecars.config.JwtConfig;
import com.sbb.ecars.domain.Account;
import com.sbb.ecars.repository.AccountRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    // 로그인 & JWT 토큰 발급
    public String authenticate(String id, String password) {
        Optional<Account> userOptional = accountRepository.findById(id);
        if (userOptional.isPresent()) {
            Account user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return generateJwtToken(user);
            }
        }
        return "INVALID_CREDENTIALS";
    }

    // JWT 토큰 생성
    private String generateJwtToken(Account user) {
        Key secretKey = getSigningKey(jwtConfig.getSecret());

        Instant now = Instant.now(); // 현재 시간
        Instant expiration = now.plusMillis(jwtConfig.getExpiration()); // 만료 시간

        return Jwts.builder()
                .claims(Map.of("sub", user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    // SecretKey 변환
    private Key getSigningKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret); // Base64 디코딩
        return Keys.hmacShaKeyFor(keyBytes);
    }
}