package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.MemberDTO;
import com.ticketcheater.webservice.entity.Member;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.jwt.TokenDTO;
import com.ticketcheater.webservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MemberDTO signup(String name, String password, String email, String nickname) {
        memberRepository.findByName(name).ifPresent(it -> {
            throw new WebApplicationException(ErrorCode.DUPLICATED_MEMBER, String.format("member is %s", name));
        });

        if(isInvalidPassword(password)) {
            throw new WebApplicationException(ErrorCode.INVALID_PASSWORD, String.format("password is %s", password));
        }

        Member member = memberRepository.save(Member.of(name, encoder.encode(password), email, nickname));
        return MemberDTO.toDTO(member);
    }

    public TokenDTO login(String name, String password) {
        MemberDTO member = memberRepository.findByName(name).map(MemberDTO::toDTO).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member with name %s not found", name)));

        if(!encoder.matches(password, member.getPassword())) {
            throw new WebApplicationException(ErrorCode.INVALID_PASSWORD, String.format("password is %s", password));
        }

        final String accessToken = jwtTokenProvider.generateAccessToken(name);
        final String refreshToken = jwtTokenProvider.generateRefreshToken(name);

        return TokenDTO.of(accessToken, refreshToken);
    }

    public void logout(String name) {
        jwtTokenProvider.deleteRefreshToken(name);
    }

    @Transactional
    public MemberDTO updateMember(String name, String password, String nickname) {
        Member member = memberRepository.findByName(name).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member with name %s not found", name)));

        if(isInvalidPassword(password)) {
            throw new WebApplicationException(ErrorCode.INVALID_PASSWORD, String.format("password is %s", password));
        }

        member.setPassword(encoder.encode(password));
        member.setNickname(nickname);

        return MemberDTO.toDTO(memberRepository.saveAndFlush(member));
    }

    @Transactional
    public void deleteMember(String name) {
        Member member = memberRepository.findByName(name).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member with name %s not found", name)));

        member.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        memberRepository.saveAndFlush(member);
    }

    // 입력한 PW 가 기존의 PW 과 일치 여부 확인
    public Boolean validateMember(String name, String password) {
        MemberDTO member = memberRepository.findByName(name).map(MemberDTO::toDTO).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member with name %s not found", name)));

        return encoder.matches(password, member.getPassword());
    }

    // PW 검증 로직, 특수 문자, 영어, 숫자가 모두 있어야 하며 8자 이상 필요
    private boolean isInvalidPassword(String password) {
        return !Pattern.matches("^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-zA-Z])(?=.*\\d).{8,}$", password);
    }

}
