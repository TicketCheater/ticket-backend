package com.ticketcheater.webservice.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupRequest {
    private String name;
    private String password;
    private String email;
    private String nickname;
}
