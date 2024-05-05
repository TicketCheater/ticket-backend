package com.ticketcheater.webservice.dto;

import com.ticketcheater.webservice.entity.Member;
import com.ticketcheater.webservice.entity.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class MemberDTO {

    private Long id;
    private String name;
    private String password;
    private String email;
    private String nickname;
    private MemberRole role;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static MemberDTO toDTO(Member member) {
        return new MemberDTO(
                member.getId(),
                member.getName(),
                member.getPassword(),
                member.getEmail(),
                member.getNickname(),
                member.getRole(),
                member.getCreatedAt(),
                member.getUpdatedAt(),
                member.getDeletedAt()
        );
    }

}
