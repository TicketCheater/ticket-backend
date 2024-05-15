package com.ticketcheater.apigateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Expired accessToken"),
    ;

    private final HttpStatus status;
    private final String message;

}
