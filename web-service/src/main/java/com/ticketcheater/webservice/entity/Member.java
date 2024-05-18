package com.ticketcheater.webservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_member_name", columnList = "name", unique = true),
        @Index(name = "idx_member_name_deletedAt", columnList = "name, deleted_at"),
})
@Entity(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberRole role = MemberRole.MEMBER;

    public static Member of(String name, String password, String email, String nickname) {
        Member member = new Member();
        member.setName(name);
        member.setPassword(password);
        member.setEmail(email);
        member.setNickname(nickname);
        return member;
    }

}
