package com.ticketcheater.webservice.controller.response.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginResponse {
    private String accessToken;
    private String refreshToken;

    public static MemberLoginResponse from(String accessToken, String refreshToken) {
        return new MemberLoginResponse(accessToken, refreshToken);
    }

}
