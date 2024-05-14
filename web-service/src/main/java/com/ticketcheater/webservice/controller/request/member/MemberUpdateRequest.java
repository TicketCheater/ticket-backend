package com.ticketcheater.webservice.controller.request.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {
    private String password;
    private String nickname;
}
