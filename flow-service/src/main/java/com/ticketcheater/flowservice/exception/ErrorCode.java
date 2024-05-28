package com.ticketcheater.flowservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_REGISTERED_MEMBER(HttpStatus.CONFLICT, "Already Registered in queue"),
    NO_SUCH_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "No such algorithm"),
    EMPTY_QUEUE(HttpStatus.NOT_FOUND, "Empty queue"),
    ;

    private final HttpStatus status;
    private final String message;

}
