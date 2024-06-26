package com.ticketcheater.webservice.controller.response.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberReissueResponse {
    private String accessToken;

    public static MemberReissueResponse from(String accessToken) {
        return new MemberReissueResponse(accessToken);
    }
}
