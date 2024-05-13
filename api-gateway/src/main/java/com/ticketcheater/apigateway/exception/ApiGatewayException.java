package com.ticketcheater.apigateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiGatewayException extends RuntimeException {

    private ErrorCode code;
    private String message;

    @Override
    public String getMessage() {
        return message != null ? String.format("%s, %s", code.getMessage(), message) : code.getMessage();
    }

}
