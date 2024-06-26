package com.ticketcheater.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.webservice.controller.request.member.*;
import com.ticketcheater.webservice.dto.MemberDTO;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.token.JwtProvider;
import com.ticketcheater.webservice.token.JwtDTO;
import com.ticketcheater.webservice.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller - 회원")
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MemberService memberService;

    @MockBean
    JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("회원 가입 정상 동작")
    @Test
    void givenMember_whenSignup_thenSavesMember() throws Exception {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberService.signup(name, password, email, nickname)).thenReturn(mock(MemberDTO.class));

        mvc.perform(post("/v1/web/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberSignupRequest(name, password, email, nickname))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("중복된 회원 정보로 회원 가입 시 오류 발생")
    @Test
    void givenDuplicatedMember_whenSignup_thenThrowsError() throws Exception {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberService.signup(name, password, email, nickname)).thenThrow(new WebApplicationException(ErrorCode.MEMBER_ALREADY_EXISTS));

        mvc.perform(post("/v1/web/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberSignupRequest(name, password, email, nickname))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.MEMBER_ALREADY_EXISTS.getStatus().value()));
    }

    @DisplayName("부적절한 비밀번호로 회원 가입 시 오류 발생")
    @Test
    void givenMemberWithInvalidPassword_whenSignup_thenThrowsError() throws Exception {
        String name = "name";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(memberService.signup(name, password, email, nickname)).thenThrow(new WebApplicationException(ErrorCode.INVALID_PASSWORD));

        mvc.perform(post("/v1/web/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberSignupRequest(name, password, email, nickname))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
    }

    @DisplayName("로그인 정상 동작")
    @Test
    void givenMember_whenLogin_thenReturnsToken() throws Exception {
        String name = "name";
        String password = "!password12";

        when(memberService.login(name, password)).thenReturn(mock(JwtDTO.class));

        mvc.perform(post("/v1/web/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberLoginRequest(name, password))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 회원 로그인 시 오류 발생")
    @Test
    void givenNonExistentMember_whenLogin_thenThrowsError() throws Exception {
        String name = "name";
        String password = "!password12";

        when(memberService.login(name, password)).thenThrow(new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        mvc.perform(post("/v1/web/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberLoginRequest(name, password))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.MEMBER_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("부적절한 비밀번호로 로그인 시 오류 발생")
    @Test
    void givenMemberWithInvalidPassword_whenLogin_thenThrowsError() throws Exception {
        String name = "name";
        String password = "!password12";

        when(memberService.login(name, password)).thenThrow(new WebApplicationException(ErrorCode.INVALID_PASSWORD));

        mvc.perform(post("/v1/web/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberLoginRequest(name, password))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
    }

    @DisplayName("올바른 비밀번호를 가진 회원 검증 시 참 반환")
    @Test
    void givenMemberWithRightPassword_whenValidate_thenReturnsTrue() throws Exception {
        String token = "dummy";
        String name = "name";
        String password = "!password12";

        when(jwtProvider.getName(anyString())).thenReturn(name);
        when(memberService.validateMember(name, password)).thenReturn(true);

        mvc.perform(post("/v1/web/members/validate")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberValidateRequest(password))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isValidate").value("true"));
    }

    @DisplayName("틀린 비밀번호를 가진 회원 검증 시 거짓 반환")
    @Test
    void givenMemberWithWrongPassword_whenValidate_thenReturnsFalse() throws Exception {
        String token = "dummy";
        String name = "name";
        String password = "!password12";

        when(jwtProvider.getName(anyString())).thenReturn(name);
        when(memberService.validateMember(name, password)).thenReturn(false);

        mvc.perform(post("/v1/web/members/validate")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberValidateRequest(password))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isValidate").value("false"));
    }

    @DisplayName("없는 회원 검증 시 오류 발생")
    @Test
    void givenNonExistentMember_whenValidate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";
        String password = "!password12";

        when(jwtProvider.getName(anyString())).thenReturn(name);
        when(memberService.validateMember(name, password)).thenThrow(new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        mvc.perform(post("/v1/web/members/validate")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberValidateRequest(password))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.MEMBER_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("접근 토큰 재발급 정상 동작")
    @Test
    void givenRefreshToken_whenReissue_thenReturnsAccessToken() throws Exception {
        String refreshToken = "dummy";

        when(jwtProvider.reissueAccessToken(refreshToken)).thenReturn(any());

        mvc.perform(post("/v1/web/members/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberReissueRequest(refreshToken))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("부적절한 Refresh 토큰으로 재발급 시 오류 발생")
    @Test
    void givenInvalidRefreshToken_whenReissue_thenThrowsError() throws Exception {
        String refreshToken = "dummy";

        when(jwtProvider.reissueAccessToken(refreshToken)).thenThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN));

        mvc.perform(post("/v1/web/members/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberReissueRequest(refreshToken))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("만료된 Refresh 토큰으로 재발급 시 오류 발생")
    @Test
    void givenExpiredRefreshToken_whenReissue_thenThrowsError() throws Exception {
        String refreshToken = "dummy";

        when(jwtProvider.reissueAccessToken(refreshToken)).thenThrow(new WebApplicationException(ErrorCode.EXPIRED_REFRESH_TOKEN));

        mvc.perform(post("/v1/web/members/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberReissueRequest(refreshToken))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.EXPIRED_REFRESH_TOKEN.getStatus().value()));
    }

    @DisplayName("회원 수정 정상 동작")
    @Test
    void givenMember_whenUpdate_thenUpdatesMember() throws Exception {
        String token = "dummy";
        String name = "name";
        String password = "!password12";
        String nickname = "nickname";

        when(jwtProvider.getName(anyString())).thenReturn(name);
        when(memberService.updateMember(name, password, nickname)).thenReturn(mock(MemberDTO.class));

        mvc.perform(patch("/v1/web/members/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberUpdateRequest(password, nickname))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 회원 수정 시 오류 발생")
    @Test
    void givenNonExistentMember_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";
        String password = "!password12";
        String nickname = "nickname";

        when(jwtProvider.getName(anyString())).thenReturn(name);
        when(memberService.updateMember(name, password, nickname)).thenThrow(new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        mvc.perform(patch("/v1/web/members/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberUpdateRequest(password, nickname))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.MEMBER_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("부적절한 비밀번호로 회원 수정 시 오류 발생")
    @Test
    void givenMemberWithInvalidPassword_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";
        String password = "!password12";
        String nickname = "nickname";

        when(jwtProvider.getName(anyString())).thenReturn(name);
        when(memberService.updateMember(name, password, nickname)).thenThrow(new WebApplicationException(ErrorCode.INVALID_PASSWORD));

        mvc.perform(patch("/v1/web/members/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberUpdateRequest(password, nickname))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
    }

    @DisplayName("회원 삭제 정상 동작")
    @Test
    void givenMember_whenDelete_thenDeletesMember() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).deleteMember(name);

        mvc.perform(patch("/v1/web/members/delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 회원 삭제 시 오류 발생")
    @Test
    void givenNonExistentMember_whenDelete_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND)).when(memberService).deleteMember(name);

        mvc.perform(patch("/v1/web/members/delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.MEMBER_NOT_FOUND.getStatus().value()));
    }

}
