package com.ticketcheater.webservice.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSignupRequest {
    private String name;
    private String password;
    private String email;
    private String nickname;
}
