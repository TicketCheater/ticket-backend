package com.ticketcheater.apigateway.filter;

import com.ticketcheater.apigateway.exception.ApiGatewayException;
import com.ticketcheater.apigateway.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();

            if(!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new ApiGatewayException(ErrorCode.INVALID_TOKEN, "No Authorization Header");
            }

            Set<String> keys = headers.keySet();
            log.info(">>>");
            keys.forEach(v -> log.info("{} = {}", v, headers.get(v)));
            log.info("<<<");

            String authorizationHeader = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            if(!isJwtValid(jwt)) {
                throw new ApiGatewayException(ErrorCode.INVALID_TOKEN, "Invalid token");
            }

            return chain.filter(exchange);
        };
    }

    /**
     * payload 를 추출해서 "name" 필드가 있는 지 확인
     * 그 전에 토큰이 만료되어서 오류가 발생했다면, EXPIRED_ACCESS_TOKEN 오류 코드를 프런트에 보내고,
     * 프런트가 /web/members/reissue 로 재발급 요청을 보내도록 유도
     */
    private boolean isJwtValid(String jwt) {
        try {
            Claims payload = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            return payload != null && !payload.get("name", String.class).isEmpty();
        } catch (ExpiredJwtException e) {
            throw new ApiGatewayException(ErrorCode.EXPIRED_ACCESS_TOKEN, "Expired accessToken");
        } catch (Exception e) {
            return false;
        }
    }

    public static class Config {}

}
