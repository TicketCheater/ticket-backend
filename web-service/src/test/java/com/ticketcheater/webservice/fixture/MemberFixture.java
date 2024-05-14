package com.ticketcheater.webservice.fixture;

import com.ticketcheater.webservice.entity.Member;
import com.ticketcheater.webservice.entity.MemberRole;

public class MemberFixture {

    public static Member get(String name, String password, String email, String nickname) {
        Member member = new Member();
        member.setId(1L);
        member.setName(name);
        member.setPassword(password);
        member.setEmail(email);
        member.setNickname(nickname);
        member.setRole(MemberRole.MEMBER);
        return member;
    }

    public static Member get(MemberRole role) {
        Member member = new Member();
        member.setId(1L);
        member.setName("name");
        member.setPassword("password");
        member.setEmail("email");
        member.setNickname("nickname");
        member.setRole(role);
        return member;
    }

}
