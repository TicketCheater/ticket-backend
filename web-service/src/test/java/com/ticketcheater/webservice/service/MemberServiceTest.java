package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.fixture.MemberFixture;
import com.ticketcheater.webservice.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Business Logic - 회원")
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService sut;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    BCryptPasswordEncoder encoder;

    @DisplayName("회원 가입 정상 동작")
    @Test
    void givenMember_whenSignup_thenSavesMember() {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByName(name)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("password_encrypt");
        when(memberRepository.save(any()))
                .thenReturn(MemberFixture.get(
                        name,
                        "password_encrypt",
                        email,
                        nickname
                ));

        Assertions.assertDoesNotThrow(() -> sut.signup(name, password, email, nickname));
    }

    @DisplayName("중복된 회원 정보로 회원 가입할 경우 오류 발생")
    @Test
    void givenDuplicatedMember_whenSignup_thenThrowsError() {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByName(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));

        WebApplicationException exception = Assertions.assertThrows(WebApplicationException.class,
                () -> sut.signup(name, password, email, nickname));

        Assertions.assertEquals(ErrorCode.DUPLICATED_MEMBER, exception.getCode());
    }

    @DisplayName("Invalid 한 PW 로 회원 가입할 경우 오류 발생")
    @Test
    void givenMemberWithInvalidPassword_whenSignup_thenThrowsError() {
        String name = "name";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByName(name)).thenReturn(Optional.empty());

        WebApplicationException exception = Assertions.assertThrows(WebApplicationException.class,
                () -> sut.signup(name, password, email, nickname));

        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, exception.getCode());
    }

    @DisplayName("올바른 PW 를 가진 회원 검증 시 참 반환")
    @Test
    void givenMemberWithRightPassword_whenValidate_thenReturnsTrue()
     {
        String name = "name";
        String password = "!password12";
        String requestPassword = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByName(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));
        when(encoder.matches(password, requestPassword)).thenReturn(true);

        Assertions.assertTrue(sut.validateMember(name, requestPassword));
    }

    @DisplayName("틀린 PW 를 가진 회원 검증 시 거짓 반환")
    @Test
    void givenMemberWithWrongPassword_whenValidate_thenReturnsFalse() {
        String name = "name";
        String password = "!password12";
        String requestPassword = "!password123";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByName(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));
        when(encoder.matches(password, requestPassword)).thenReturn(false);

        Assertions.assertFalse(sut.validateMember(name, requestPassword));
    }

    @DisplayName("없는 회원 검증 시 오류 발생")
    @Test
    void givenNonExistentMember_whenValidate_thenThrowsError() {
        String name = "name";
        String requestPassword = "!password12";

        when(memberRepository.findByName(name)).thenReturn(Optional.empty());
        WebApplicationException exception = Assertions.assertThrows(WebApplicationException.class,
                () -> sut.validateMember(name, requestPassword));

        Assertions.assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getCode());
    }

}
