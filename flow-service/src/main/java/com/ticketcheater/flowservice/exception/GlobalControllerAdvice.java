package com.ticketcheater.flowservice.exception;

import com.ticketcheater.flowservice.controller.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(FlowApplicationException.class)
    Mono<ResponseEntity<?>> errorHandler(FlowApplicationException e) {
        log.error("Error occurs {}", e.toString());
        return Mono.just(ResponseEntity
                .status(e.getCode().getStatus())
                .body(Response.error(e.getCode().name())));
    }

}
