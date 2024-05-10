package com.ticketcheater.webservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TokenCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void setToken(String name, String token, long timeMs) {
        log.info("Set Refresh Token to Redis {}({})", token, name);
        redisTemplate.opsForValue().set(name, token, timeMs, TimeUnit.MILLISECONDS);
    }

    public String getToken(String name) {
        String refreshToken = redisTemplate.opsForValue().get(name);
        log.info("Get Refresh Token from Redis {}({})", refreshToken, name);
        return refreshToken;
    }

    public void deleteToken(String name) {
        log.info("Delete Refresh Token to Redis ({})", name);
        redisTemplate.opsForValue().getAndDelete(name);
    }

}
