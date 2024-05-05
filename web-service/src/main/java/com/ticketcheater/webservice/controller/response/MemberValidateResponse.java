package com.ticketcheater.webservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberValidateResponse {
    private Boolean isValidate;

    public static MemberValidateResponse from(Boolean isValidate) {
        return new MemberValidateResponse(isValidate);
    }

}
