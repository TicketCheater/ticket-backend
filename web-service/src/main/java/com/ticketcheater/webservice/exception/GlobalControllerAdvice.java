package com.ticketcheater.webservice.exception;

import com.ticketcheater.webservice.controller.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(WebApplicationException.class)
    public ResponseEntity<?> errorHandler(WebApplicationException e) {
        return ResponseEntity.status(e.getCode().getStatus())
                .body(Response.error(e.getCode().name()));
    }

}
