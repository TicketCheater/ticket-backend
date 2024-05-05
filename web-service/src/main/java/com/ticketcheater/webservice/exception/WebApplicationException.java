package com.ticketcheater.webservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebApplicationException extends RuntimeException {

    private ErrorCode code;
    private String message;

    public WebApplicationException(ErrorCode code) {
        this.code = code;
        this.message = null;
    }

    @Override
    public String getMessage() {
        return message != null ? String.format("%s, %s", code.getMessage(), message) : code.getMessage();
    }

}
