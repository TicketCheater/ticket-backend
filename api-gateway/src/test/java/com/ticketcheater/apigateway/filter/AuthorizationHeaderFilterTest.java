package com.ticketcheater.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * AuthorizationHeaderFilter 가 정상적으로 작동하는지 작성한 테스트
 * AuthorizationHeaderFilter 에 있는 @Value("${jwt.secret-key}) 에 대한 해결책
 * secretKey 는 여기서 테스트 용도로 새로 만들어준다. 32글자만 넘으면 된다.
 * 참고로 나는 가장 긴 영단어인 진폐증을 사용했다. (45글자)
 * 그리고 setUp() 메소드에다가 ReflectionTestUtils 를 사용하여 @Value 로 되어있는 secretKey 필드 값 설정
 */
@SpringBootTest
class AuthorizationHeaderFilterTest {

    @Autowired
    private AuthorizationHeaderFilter authorizationHeaderFilter;

    private final String secretKey = "pneumonoultramicroscopicsilicovolcanoconiosis";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authorizationHeaderFilter, "secretKey", secretKey);
    }

    @DisplayName("인증 헤더가 없으면 오류 발생")
    @Test
    void givenNoAuthorizationHeader_whenFilter_thenThrowsError() {
        ServerWebExchange exchange = createMockServerWebExchange("");
        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);

        authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config()).filter(exchange, chain);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @DisplayName("Invalid 한 토큰이 오면 오류 발생")
    @Test
    void givenInvalidToken_whenFilter_thenThrowsError() {
        String invalidJwt = "invalid.jwt.token";
        ServerWebExchange exchange = createMockServerWebExchange("Bearer " + invalidJwt);
        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);

        authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config()).filter(exchange, chain);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @DisplayName("만료된 토큰이 오면 오류 발생")
    @Test
    void givenExpiredToken_whenFilter_thenThrowsError() {
        String expiredJwt = createExpiredJwtToken();
        ServerWebExchange exchange = createMockServerWebExchange("Bearer " + expiredJwt);
        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);

        authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config()).filter(exchange, chain);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @DisplayName("Valid 한 토큰이 오면 필터 통과")
    @Test
    void givenValidToken_whenFilter_thenReturnSuccess() {
        String validJwt = createValidJwtToken();
        ServerWebExchange exchange = createMockServerWebExchange("Bearer " + validJwt);
        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);

        authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config()).filter(exchange, chain);

        assertNull(exchange.getResponse().getStatusCode());
    }

    private ServerWebExchange createMockServerWebExchange(String authorizationHeader) {
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .build();
        return MockServerWebExchange.from(request);
    }

    private String createValidJwtToken() {
        Claims claims = Jwts.claims().add("name", "testUser").build();

        return Jwts.builder()
                .claims(claims)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();
    }

    private String createExpiredJwtToken() {
        Claims claims = Jwts.claims().add("name", "testUser").build();

        return Jwts.builder()
                .claims(claims)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .expiration(new Date(System.currentTimeMillis() - 3600000))
                .compact();
    }

}
