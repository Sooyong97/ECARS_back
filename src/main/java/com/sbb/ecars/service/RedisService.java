package com.sbb.ecars.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRATION_TIME = 5; // 5분

    // 인증 코드 저장 (5분 후 자동 삭제)
    public void saveAuthCode(String email, String authCode) {
        redisTemplate.opsForValue().set(email, authCode, EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    // 인증 코드 조회
    public String getAuthCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    // 인증 코드 삭제
    public void deleteAuthCode(String email) {
        redisTemplate.delete(email);
    }
}