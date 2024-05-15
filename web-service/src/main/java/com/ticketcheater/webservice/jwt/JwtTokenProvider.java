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
        tokenCacheRepository.setToken(name, refreshToken, refreshExpirationTimeMs);
        return refreshToken;
    }

    public void deleteRefreshToken(String name) {
        tokenCacheRepository.deleteToken(name);
    }

    /**
     * access token 을 재발급하는 로직
     * refresh token 에서 name 을 추출 (이 때 오류가 발생하면 INVALID_TOKEN)
     * redis 에서 토큰을 가져와서 토큰이 없거나 일치하지 않으면 오류 발생
     * 오류가 없으면 access token 재발급
     */
    public String reissueAccessToken(String refreshToken) {
        String name;

        try {
            name = getName(refreshToken, refreshKey);
        } catch (Exception e) {
            throw new WebApplicationException(ErrorCode.INVALID_TOKEN);
        }

        String token = tokenCacheRepository.getToken(name);

        if(Objects.isNull(token)) {
            throw new WebApplicationException(
                    ErrorCode.EXPIRED_REFRESH_TOKEN, String.format("The refresh token of member %s has expired", name)
            );
        }

        if(!refreshToken.equals(token)) {
            throw new WebApplicationException(
                    ErrorCode.INVALID_TOKEN, String.format("The refresh token of member %s is not valid", name)
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
