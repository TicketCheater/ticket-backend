package com.ticketcheater.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.webservice.controller.request.team.TeamCreateRequest;
import com.ticketcheater.webservice.controller.request.team.TeamUpdateRequest;
import com.ticketcheater.webservice.dto.TeamDTO;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.MemberService;
import com.ticketcheater.webservice.service.TeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller - 팀")
@SpringBootTest
@AutoConfigureMockMvc
class TeamControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MemberService memberService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    TeamService teamService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("팀 생성 정상 동작")
    @Test
    void givenTeam_whenCreate_thenCreatesTeam() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(teamService.createTeam(any())).thenReturn(mock(TeamDTO.class));

        mvc.perform(post("/v1/web/teams/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TeamCreateRequest("팀이름"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("관리자가 아닌 회원이 팀 생성 시 오류 발생")
    @Test
    void givenNonAdminMember_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        when(teamService.createTeam(any())).thenReturn(mock(TeamDTO.class));

        mvc.perform(post("/v1/web/teams/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TeamCreateRequest("팀이름"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("부적절한 이름으로 팀 생성 시 오류 발생")
    @Test
    void givenTeamWithInvalidName_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(teamService.createTeam(any())).thenThrow(new WebApplicationException(ErrorCode.INVALID_TEAM));

        mvc.perform(post("/v1/web/teams/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TeamCreateRequest("invalid"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TEAM.getStatus().value()));
    }

    @DisplayName("팀 수정 정상 동작")
    @Test
    void givenTeam_whenUpdate_thenUpdatesTeam() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(teamService.updateTeam(eq(1L), any())).thenReturn(mock(TeamDTO.class));

        mvc.perform(patch("/v1/web/teams/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TeamUpdateRequest("새팀이름"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 팀 수정 시 오류 발생")
    @Test
    void givenNonExistentTeam_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(teamService.updateTeam(eq(1L), any())).thenThrow(new WebApplicationException(ErrorCode.TEAM_NOT_FOUND));

        mvc.perform(patch("/v1/web/teams/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TeamUpdateRequest("새팀이름"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.TEAM_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("부적절한 이름으로 팀 생성 시 오류 발생")
    @Test
    void givenTeamWithInvalidName_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(teamService.updateTeam(eq(1L), any())).thenThrow(new WebApplicationException(ErrorCode.INVALID_TEAM));

        mvc.perform(patch("/v1/web/teams/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TeamUpdateRequest("invalid"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TEAM.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 회원이 팀 수정 시 오류 발생")
    @Test
    void givenNonAdminMember_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        when(teamService.updateTeam(eq(1L), any())).thenReturn(mock(TeamDTO.class));

        mvc.perform(patch("/v1/web/teams/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TeamUpdateRequest("새팀이름"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("팀 삭제 정상 동작")
    @Test
    void givenTeam_whenDelete_thenDeletesTeam() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doNothing().when(teamService).deleteTeam(eq(1L));

        mvc.perform(patch("/v1/web/teams/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 팀 삭제 시 오류 발생")
    @Test
    void givenNonExistentTeam_whenDelete_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.TEAM_NOT_FOUND)).when(teamService).deleteTeam(eq(1L));

        mvc.perform(patch("/v1/web/teams/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.TEAM_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 회원이 팀 삭제 시 오류 발생")
    @Test
    void givenNonAdminMember_whenDelete_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        doNothing().when(teamService).deleteTeam(eq(1L));

        mvc.perform(patch("/v1/web/teams/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("팀 복구 정상 동작")
    @Test
    void givenTeam_whenRestore_thenRestoresTeam() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doNothing().when(teamService).restoreTeam(eq(1L));

        mvc.perform(patch("/v1/web/teams/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("존재하는 팀 삭제 시 오류 발생")
    @Test
    void givenExistentTeam_whenRestore_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.TEAM_ALREADY_EXISTS)).when(teamService).restoreTeam(eq(1L));

        mvc.perform(patch("/v1/web/teams/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.TEAM_ALREADY_EXISTS.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 회원이 팀 복구 시 오류 발생")
    @Test
    void givenNonAdminMember_whenRestore_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        doNothing().when(teamService).restoreTeam(eq(1L));

        mvc.perform(patch("/v1/web/teams/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

}
