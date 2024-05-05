package com.ticketcheater.webservice.controller.response;

import com.ticketcheater.webservice.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSignupResponse {

    private Long id;
    private String name;

    public static MemberSignupResponse from(MemberDTO member) {
        return new MemberSignupResponse(member.getId(), member.getName());
    }

}
