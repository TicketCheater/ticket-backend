package com.ticketcheater.flowservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

public class JwtUtils {

    private JwtUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static Claims extractAllClaims(String jwt, String key) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public static String getName(String header, String key) {
        String jwt = header.substring(7);
        return extractAllClaims(jwt, key).get("name", String.class);
    }

}
