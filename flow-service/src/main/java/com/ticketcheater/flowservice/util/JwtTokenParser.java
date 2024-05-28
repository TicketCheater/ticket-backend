package com.ticketcheater.flowservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenParser {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String getName(String header) {
        return JwtUtils.getName(header, secretKey);
    }
}
