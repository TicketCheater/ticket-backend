package com.ticketcheater.webservice.entity;

import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;

public enum PaymentMethod {

    CARD,
    CASH,
    TRANSFER;

    public static PaymentMethod fromString(String param) {
        try {
            return PaymentMethod.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(ErrorCode.PAYMENT_METHOD_NOT_FOUND);
        }
    }

}
