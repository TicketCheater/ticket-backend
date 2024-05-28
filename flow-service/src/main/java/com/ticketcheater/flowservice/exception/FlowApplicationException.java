package com.ticketcheater.flowservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FlowApplicationException extends RuntimeException {

    private ErrorCode code;
    private String message;

    public FlowApplicationException(ErrorCode code) {
        this.code = code;
        this.message = null;
    }

}
