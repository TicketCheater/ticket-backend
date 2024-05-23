package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.config.TestContainerConfig;
import com.ticketcheater.webservice.entity.MemberRole;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.fixture.MemberFixture;
import com.ticketcheater.webservice.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Business Logic - 회원")
@ExtendWith(TestContainerConfig.class)
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

        assertDoesNotThrow(() -> sut.signup(name, password, email, nickname));
    }

    @DisplayName("중복된 회원 정보로 회원 가입 시 오류 발생")
    @Test
    void givenDuplicatedMember_whenSignup_thenThrowsError() {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByName(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.signup(name, password, email, nickname)
        );

        assertEquals(ErrorCode.MEMBER_ALREADY_EXISTS, exception.getCode());
    }

    @DisplayName("부적절한 비밀번호로 회원 가입 시 오류 발생")
    @Test
    void givenMemberWithInvalidPassword_whenSignup_thenThrowsError() {
        String name = "name";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByName(name)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.signup(name, password, email, nickname)
        );

        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getCode());
    }

    @DisplayName("로그인 정상 동작")
    @Test
    void givenMember_whenLogin_thenReturnsToken() {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));
        when(encoder.matches(password, "!password12")).thenReturn(true);

        assertDoesNotThrow(() -> sut.login(name, password));
    }

    @DisplayName("없는 회원 로그인 시 오류 발생")
    @Test
    void givenNonExistentMember_whenLogin_thenThrowsError() {
        String name = "name";
        String password = "!password12";

        when(memberRepository.findByNameAndDeletedAtIsNull(name)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.login(name, password)
        );

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getCode());
    }

    @DisplayName("부적절한 비밀번호로 로그인 시 오류 발생")
    @Test
    void givenMemberWithInvalidPassword_whenLogin_thenThrowsError() {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));
        when(encoder.matches(password, "!password12")).thenReturn(false);

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.login(name, password)
        );

        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getCode());
    }

    @DisplayName("올바른 비밀번호를 가진 회원 검증 시 참 반환")
    @Test
    void givenMemberWithRightPassword_whenValidate_thenReturnsTrue()
     {
        String name = "name";
        String password = "!password12";
        String requestPassword = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));
        when(encoder.matches(password, requestPassword)).thenReturn(true);

        Assertions.assertTrue(sut.validateMember(name, requestPassword));
    }

    @DisplayName("틀린 비밀번호를 가진 회원 검증 시 거짓 반환")
    @Test
    void givenMemberWithWrongPassword_whenValidate_thenReturnsFalse() {
        String name = "name";
        String password = "!password12";
        String requestPassword = "!password123";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));
        when(encoder.matches(password, requestPassword)).thenReturn(false);

        Assertions.assertFalse(sut.validateMember(name, requestPassword));
    }

    @DisplayName("없는 회원 검증 시 오류 발생")
    @Test
    void givenNonExistentMember_whenValidate_thenThrowsError() {
        String name = "name";
        String requestPassword = "!password12";

        when(memberRepository.findByNameAndDeletedAtIsNull(name)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.validateMember(name, requestPassword)
        );

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getCode());
    }

    @DisplayName("회원 수정 정상 동작")
    @Test
    void givenMember_whenUpdate_thenUpdatesMember() {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));
        when(encoder.encode(password)).thenReturn("password_encrypt");
        when(memberRepository.saveAndFlush(any()))
                .thenReturn(MemberFixture.get(
                        name,
                        "password_encrypt",
                        email,
                        nickname
                ));

        assertDoesNotThrow(() -> sut.updateMember(name, password, nickname));
    }

    @DisplayName("없는 회원 수정 시 오류 발생")
    @Test
    void givenNonExistentMember_whenUpdate_thenThrowsError() {
        String name = "name";
        String password = "!password12";
        String nickname = "nickname";

        when(memberRepository.findByNameAndDeletedAtIsNull(name)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.updateMember(name, password, nickname)
        );

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getCode());
    }

    @DisplayName("부적절한 비밀번호로 회원 수정 시 오류 발생")
    @Test
    void givenMemberWithInvalidPassword_whenUpdate_thenThrowsError() {
        String name = "name";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.updateMember(name, password, nickname)
        );

        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getCode());
    }

    @DisplayName("회원 삭제 정상 동작")
    @Test
    void givenMember_whenDelete_thenDeletesMember() {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.of(MemberFixture.get(name, password, email, nickname)));
        when(memberRepository.saveAndFlush(any()))
                .thenReturn(MemberFixture.get(
                        name,
                        "password_encrypt",
                        email,
                        nickname
                ));

        assertDoesNotThrow(() -> sut.deleteMember(name));
    }

    @DisplayName("없는 회원 삭제 시 오류 발생")
    @Test
    void givenNonExistentMember_whenDelete_thenThrowsError() {
        String name = "name";

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.deleteMember(name)
        );

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getCode());
    }

    @DisplayName("관리자 회원 검증 시 성공")
    @Test
    void givenAdminMember_whenIsAdmin_thenSuccess() {
        String name = "name";
        MemberRole role = MemberRole.ADMIN;

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.of(MemberFixture.get(role)));

        assertDoesNotThrow(() -> sut.isAdmin(name));
    }

    @DisplayName("일반 회원 검증 시 오류 발생")
    @Test
    void givenNonAdminMember_whenIsAdmin_thenThrowsError() {
        String name = "name";
        MemberRole role = MemberRole.MEMBER;

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.of(MemberFixture.get(role)));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.isAdmin(name)
        );

        assertEquals(ErrorCode.INVALID_TOKEN, exception.getCode());
    }

    @DisplayName("없는 회원 검증 시 오류 발생")
    @Test
    void givenNonExistentMember_whenIsAdmin_thenThrowsError() {
        String name = "name";

        when(memberRepository.findByNameAndDeletedAtIsNull(name))
                .thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.isAdmin(name)
        );

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getCode());
    }

}
