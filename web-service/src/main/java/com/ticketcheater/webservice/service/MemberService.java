package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.MemberDTO;
import com.ticketcheater.webservice.entity.Member;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public MemberDTO signup(String name, String password, String email, String nickname) {
        memberRepository.findByName(name).ifPresent(it -> {
            throw new WebApplicationException(ErrorCode.DUPLICATED_MEMBER, String.format("member is %s", name));
        });

        if(!validatePassword(password)) {
            throw new WebApplicationException(ErrorCode.INVALID_PASSWORD, String.format("password is %s", password));
        }

        Member member = memberRepository.save(Member.of(name, encoder.encode(password), email, nickname));
        return MemberDTO.toDTO(member);
    }

    // 입력한 PW 가 기존의 PW 과 일치 여부 확인
    public Boolean validateMember(String name, String password) {
        Member member = findMember(name);
        return encoder.matches(password, member.getPassword());
    }

    // PW 검증 로직, 특수 문자, 영어, 숫자가 모두 있어야 하며 8자 이상 필요
    private boolean validatePassword(String password) {
        return Pattern.matches("^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-zA-Z])(?=.*\\d).{8,}$", password);
    }

    private Member findMember(String name) {
        return memberRepository.findByName(name).orElseThrow(() ->
                new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member is %s", name)));
    }

}
