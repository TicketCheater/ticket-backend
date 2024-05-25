package com.ticketcheater.webservice.controller.response.member;

import com.ticketcheater.webservice.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUpdateResponse {
    private Long memberId;
    private String memberName;

    public static MemberUpdateResponse from(MemberDTO member) {
        return new MemberUpdateResponse(member.getId(), member.getName());
    }
}
