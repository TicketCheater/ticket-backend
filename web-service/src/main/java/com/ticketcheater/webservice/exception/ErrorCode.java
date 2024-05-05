package com.ticketcheater.webservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_MEMBER(HttpStatus.CONFLICT, "Duplicated member"),
    INVALID_PASSWORD(HttpStatus.FORBIDDEN, "Invalid password"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member not found");

    private final HttpStatus status;
    private final String message;

}
