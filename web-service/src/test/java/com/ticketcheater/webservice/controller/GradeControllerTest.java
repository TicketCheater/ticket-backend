package com.ticketcheater.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.webservice.controller.request.grade.GradeCreateRequest;
import com.ticketcheater.webservice.dto.GradeDTO;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.GradeService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller - 등급")
@SpringBootTest
@AutoConfigureMockMvc
class GradeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MemberService memberService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    GradeService gradeService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("등급 생성 정상 동작")
    @Test
    void givenGrade_whenCreate_thenCreatesGrade() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gradeService.createGrade(eq(1L), eq("VIP"))).thenReturn(mock(GradeDTO.class));

        mvc.perform(post("/v1/web/grades/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GradeCreateRequest(1L, "VIP"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("관리자가 아닌 회원이 등급 생성 시 오류 발생")
    @Test
    void givenNonAdminMember_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);

        mvc.perform(post("/v1/web/grades/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GradeCreateRequest(1L, "VIP"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("없는 장소의 등급 생성 시 오류 발생")
    @Test
    void givenNonExistentPlace_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gradeService.createGrade(eq(999L), eq("VIP"))).thenThrow(new WebApplicationException(ErrorCode.PLACE_NOT_FOUND));

        mvc.perform(post("/v1/web/grades/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GradeCreateRequest(999L, "VIP"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.PLACE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("이미 존재하는 등급 생성 시 오류 발생")
    @Test
    void givenExistingGrade_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gradeService.createGrade(eq(1L), eq("VIP"))).thenThrow(new WebApplicationException(ErrorCode.GRADE_ALREADY_EXISTS));

        mvc.perform(post("/v1/web/grades/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GradeCreateRequest(1L, "VIP"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GRADE_ALREADY_EXISTS.getStatus().value()));
    }

    @DisplayName("등급 수정 정상 동작")
    @Test
    void givenGrade_whenUpdate_thenUpdatesGrade() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gradeService.updateGrade(eq(1L), eq(1L), eq("VVIP"))).thenReturn(mock(GradeDTO.class));

        mvc.perform(patch("/v1/web/grades/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GradeCreateRequest(1L, "VVIP"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 장소로 등급 수정 시 오류 발생")
    @Test
    void givenNonExistentPlace_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gradeService.updateGrade(eq(1L), eq(999L), eq("VVIP"))).thenThrow(new WebApplicationException(ErrorCode.PLACE_NOT_FOUND));

        mvc.perform(patch("/v1/web/grades/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GradeCreateRequest(999L, "VVIP"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.PLACE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("없는 등급 수정 시 오류 발생")
    @Test
    void givenNonExistentGrade_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gradeService.updateGrade(eq(1L), eq(1L), eq("VVIP"))).thenThrow(new WebApplicationException(ErrorCode.GRADE_NOT_FOUND));

        mvc.perform(patch("/v1/web/grades/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GradeCreateRequest(1L, "VVIP"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GRADE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("등급 삭제 정상 동작")
    @Test
    void givenGrade_whenDelete_thenDeletesGrade() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doNothing().when(gradeService).deleteGrade(eq(1L));

        mvc.perform(patch("/v1/web/grades/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 등급 삭제 시 오류 발생")
    @Test
    void givenNonExistentGrade_whenDelete_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.GRADE_NOT_FOUND)).when(gradeService).deleteGrade(eq(1L));

        mvc.perform(patch("/v1/web/grades/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GRADE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("등급 복구 정상 동작")
    @Test
    void givenGrade_whenRestore_thenRestoresGrade() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doNothing().when(gradeService).restoreGrade(eq(1L));

        mvc.perform(patch("/v1/web/grades/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("존재하는 등급 복구 시 오류 발생")
    @Test
    void givenExistentGrade_whenRestore_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.GRADE_ALREADY_EXISTS)).when(gradeService).restoreGrade(eq(1L));

        mvc.perform(patch("/v1/web/grades/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GRADE_ALREADY_EXISTS.getStatus().value()));
    }
}
