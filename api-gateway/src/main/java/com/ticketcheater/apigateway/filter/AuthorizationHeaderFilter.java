package com.ticketcheater.apigateway.filter;

import com.ticketcheater.apigateway.exception.ApiGatewayException;
import com.ticketcheater.apigateway.exception.ErrorCode;
import io.jsonwebtoken.Claims;
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
import java.util.Date;
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
                throw new ApiGatewayException(ErrorCode.INVALID_TOKEN, "Invalid Token");
            }

            return chain.filter(exchange);
        };
    }

    private boolean isJwtValid(String jwt) {
        boolean isJwtValid = true;
        Claims payload = null;

        try {
            payload = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        } catch (Exception e) {
            isJwtValid = false;
        }

        if(payload == null || payload.get("name", String.class).isEmpty() || payload.getExpiration().before(new Date())) {
            isJwtValid = false;
        }

        return isJwtValid;
    }

    public static class Config {}

}
