package com.ticketcheater.webservice.jwt;

import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.TokenCacheRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final TokenCacheRepository tokenCacheRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-key}")
    private String refreshKey;

    @Value("${jwt.token.access-expiration-time}")
    private long accessExpirationTimeMs;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshExpirationTimeMs;

    private static Claims extractAllClaims(String token, String key) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static String getName(String token, String key) {
        return extractAllClaims(token, key).get("name", String.class);
    }

    public String getName(String header) {
        String token = header.substring(7);
        return getName(token, secretKey);
    }

    public String generateAccessToken(String name) {
        return doGenerateToken(name, accessExpirationTimeMs, secretKey);
    }

    public String generateRefreshToken(String name) {
        String refreshToken = doGenerateToken(name, refreshExpirationTimeMs, refreshKey);
        tokenCacheRepository.setToken(name, refreshKey, refreshExpirationTimeMs);
        return refreshToken;
    }

    public void deleteRefreshToken(String name) {
        tokenCacheRepository.deleteToken(name);
    }

    public String reissueAccessToken(String name, String rtk) {
        String refreshToken = tokenCacheRepository.getToken(name);
        if (Objects.isNull(refreshToken)) {
            throw new WebApplicationException(
                    ErrorCode.EXPIRED_TOKEN, String.format("The refresh token of username %s has expired", name)
            );
        }
        if (!name.equals(getName(rtk, refreshKey))) {
            throw new WebApplicationException(
                    ErrorCode.INVALID_TOKEN, String.format("The refresh token of username %s is not valid", name)
            );
        }
        return generateAccessToken(name);
    }

    private static String doGenerateToken(String name, long expireTime, String key) {
        Claims claims = Jwts.claims().add("name", name).build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

}
