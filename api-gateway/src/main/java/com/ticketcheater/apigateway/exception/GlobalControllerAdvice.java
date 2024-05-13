package com.ticketcheater.apigateway.exception;

import com.ticketcheater.apigateway.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ApiGatewayException.class)
    public ResponseEntity<?> errorHandler(ApiGatewayException e) {
        return ResponseEntity.status(e.getCode().getStatus())
                .body(Response.error(e.getCode().name()));
    }

}
