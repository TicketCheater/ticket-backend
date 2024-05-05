package com.ticketcheater.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.webservice.controller.request.MemberSignupRequest;
import com.ticketcheater.webservice.controller.request.MemberValidateRequest;
import com.ticketcheater.webservice.dto.MemberDTO;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller - 회원")
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("회원 가입 정상 동작")
    @Test
    void givenMember_whenSignup_thenSuccess() throws Exception {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberService.signup(name, password, email, nickname)).thenReturn(mock(MemberDTO.class));

        mvc.perform(post("/v1/web/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberSignupRequest(name,password,email,nickname))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("중복된 회원 정보로 회원 가입할 경우 오류 발생")
    @Test
    void givenDuplicatedMember_whenSignup_thenThrowsError() throws Exception {
        String name = "name";
        String password = "!password12";
        String email = "email";
        String nickname = "nickname";

        when(memberService.signup(name, password, email, nickname)).thenThrow(new WebApplicationException(ErrorCode.DUPLICATED_MEMBER));

        mvc.perform(post("/v1/web/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberSignupRequest(name,password,email,nickname))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DUPLICATED_MEMBER.getStatus().value()));
    }

    @DisplayName("Invalid 한 PW 로 회원 가입할 경우 오류 발생")
    @Test
    void givenMemberWithInvalidPassword_whenSignup_thenThrowsError() throws Exception {
        String name = "name";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(memberService.signup(name, password, email, nickname)).thenThrow(new WebApplicationException(ErrorCode.INVALID_PASSWORD));

        mvc.perform(post("/v1/web/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberSignupRequest(name,password,email,nickname))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
    }

    @DisplayName("올바른 PW 를 가진 회원 검증 시 참 반환")
    @Test
    void givenMemberWithRightPassword_whenValidate_thenReturnsTrue() throws Exception {
        String name = "name";
        String password = "!password12";

        when(memberService.validateMember(name, password)).thenReturn(Boolean.TRUE);

        mvc.perform(post("/v1/web/members/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberValidateRequest(name,password))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isValidate").value("true"));
    }

    @DisplayName("틀린 PW 를 가진 회원 검증 시 거짓 반환")
    @Test
    void givenMemberWithWrongPassword_whenValidate_thenReturnsFalse() throws Exception {
        String name = "name";
        String password = "!password12";

        when(memberService.validateMember(name, password)).thenReturn(Boolean.FALSE);

        mvc.perform(post("/v1/web/members/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberValidateRequest(name,password))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isValidate").value("false"));
    }

    @DisplayName("없는 회원 검증 시 오류 발생")
    @Test
    void givenNonExistentMember_whenValidate_thenThrowsError() throws Exception {
        String name = "name";
        String password = "!password12";

        when(memberService.validateMember(name, password)).thenThrow(new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        mvc.perform(post("/v1/web/members/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberValidateRequest(name,password))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.MEMBER_NOT_FOUND.getStatus().value()));
    }

}
