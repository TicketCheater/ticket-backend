package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.MemberDTO;
import com.ticketcheater.webservice.entity.Member;
import com.ticketcheater.webservice.entity.MemberRole;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.jwt.TokenDTO;
import com.ticketcheater.webservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MemberDTO signup(String name, String password, String email, String nickname) {
        memberRepository.findByName(name).ifPresent(it -> {
            throw new WebApplicationException(ErrorCode.MEMBER_ALREADY_EXISTS, String.format("member with name %s already exists", name));
        });

        if(isInvalidPassword(password)) {
            throw new WebApplicationException(ErrorCode.INVALID_PASSWORD, "password is not valid");
        }

        Member member = memberRepository.save(Member.of(name, encoder.encode(password), email, nickname));

        log.info("signup method executed successfully for member: name={}, email={}, nickname={}", name, email, nickname);

        return MemberDTO.toDTO(member);
    }

    public TokenDTO login(String name, String password) {
        MemberDTO member = memberRepository.findByName(name).map(MemberDTO::toDTO).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member with name %s not found", name))
        );

        if(!encoder.matches(password, member.getPassword())) {
            throw new WebApplicationException(ErrorCode.INVALID_PASSWORD, "password is not valid");
        }

        final String accessToken = jwtTokenProvider.generateAccessToken(name);
        final String refreshToken = jwtTokenProvider.generateRefreshToken(name);

        log.info("login method executed successfully for member: name={}", name);

        return TokenDTO.of(accessToken, refreshToken);
    }

    public void logout(String name) {
        jwtTokenProvider.deleteRefreshToken(name);
        log.info("logout method executed successfully for member: name={}", name);
    }

    @Transactional
    public MemberDTO updateMember(String name, String password, String nickname) {
        Member member = memberRepository.findByNameAndDeletedAtIsNull(name).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member with name %s not found", name))
        );

        if(isInvalidPassword(password)) {
            throw new WebApplicationException(ErrorCode.INVALID_PASSWORD, "password is not valid");
        }

        member.setPassword(encoder.encode(password));
        member.setNickname(nickname);

        memberRepository.saveAndFlush(member);

        log.info("update member method executed successfully for member: name={}, nickname={}", name, nickname);

        return MemberDTO.toDTO(member);
    }

    @Transactional
    public void deleteMember(String name) {
        Member member = memberRepository.findByNameAndDeletedAtIsNull(name).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member with name %s not found", name))
        );

        member.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        memberRepository.saveAndFlush(member);

        log.info("delete member method executed successfully for member: name={}", name);
    }

    // 입력한 PW 가 기존의 PW 과 일치 여부 확인
    public Boolean validateMember(String name, String password) {
        MemberDTO member = memberRepository.findByNameAndDeletedAtIsNull(name).map(MemberDTO::toDTO).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member with name %s not found", name))
        );

        Boolean isValid = encoder.matches(password, member.getPassword());

        log.info("validate member method executed successfully for member: name={}", name);

        return isValid;
    }

    // PW 검증 로직, 특수 문자, 영어, 숫자가 모두 있어야 하며 8자 이상 필요
    private boolean isInvalidPassword(String password) {
        return !Pattern.matches("^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-zA-Z])(?=.*\\d).{8,}$", password);
    }

    public void isAdmin(String name) {
        MemberDTO member = memberRepository.findByNameAndDeletedAtIsNull(name).map(MemberDTO::toDTO).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member with name %s not found", name))
        );

        if(!member.getRole().equals(MemberRole.ADMIN)) {
          throw new WebApplicationException(ErrorCode.INVALID_TOKEN, String.format("member %s does not have permission", name));
        }

        log.info("member with name {} has the necessary permissions", name);
    }

}
