package com.ticketcheater.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.webservice.controller.request.game.GameCreateRequest;
import com.ticketcheater.webservice.controller.request.game.GameUpdateRequest;
import com.ticketcheater.webservice.dto.GameDTO;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.GameService;
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

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller - 게임")
@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MemberService memberService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("게임 생성 정상 동작")
    @Test
    void givenGame_whenCreate_thenCreatesGame() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gameService.createGame(any())).thenReturn(mock(GameDTO.class));

        mvc.perform(post("/v1/web/games/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameCreateRequest("baseball", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("관리자가 아닌 회원이 게임 생성 시 오류 발생")
    @Test
    void givenNonAdminMember_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        when(gameService.createGame(any())).thenReturn(mock(GameDTO.class));

        mvc.perform(post("/v1/web/games/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameCreateRequest("baseball", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("타입이 부적절한 게임 생성 시 오류 발생")
    @Test
    void givenGameWithInvalidType_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gameService.createGame(any())).thenThrow(new WebApplicationException(ErrorCode.GAME_TYPE_NOT_FOUND));

        mvc.perform(post("/v1/web/games/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameCreateRequest("wrong", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GAME_TYPE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("팀이 부적절한 게임 생성 시 오류 발생")
    @Test
    void givenGameWithInvalidTeam_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gameService.createGame(any())).thenThrow(new WebApplicationException(ErrorCode.TEAM_NOT_FOUND));

        mvc.perform(post("/v1/web/games/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameCreateRequest("baseball", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.TEAM_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("장소가 부적절한 게임 생성 시 오류 발생")
    @Test
    void givenGameWithInvalidPlace_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gameService.createGame(any())).thenThrow(new WebApplicationException(ErrorCode.PLACE_NOT_FOUND));

        mvc.perform(post("/v1/web/games/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameCreateRequest("baseball", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.PLACE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("게임 조회 정상 동작")
    @Test
    void givenTeamId_whenRead_thenReadsTeam() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(gameService.getGamesByHome(eq(1L))).thenReturn(List.of(mock(GameDTO.class)));

        mvc.perform(get("/v1/web/games/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 팀의 게임 조회 시 오류 발생")
    @Test
    void givenNonExistentTeam_whenRead_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(gameService.getGamesByHome(eq(1L))).thenThrow(new WebApplicationException(ErrorCode.TEAM_NOT_FOUND));

        mvc.perform(get("/v1/web/games/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.TEAM_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("게임 수정 정상 동작")
    @Test
    void givenGame_whenUpdate_thenUpdatesGame() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gameService.updateGame(eq(1L), any())).thenReturn(mock(GameDTO.class));

        mvc.perform(patch("/v1/web/games/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameUpdateRequest("baseball", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 게임 수정 시 오류 발생")
    @Test
    void givenNonExistentGame_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gameService.updateGame(eq(1L), any())).thenThrow(new WebApplicationException(ErrorCode.GAME_NOT_FOUND));

        mvc.perform(patch("/v1/web/games/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameUpdateRequest("baseball", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GAME_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("팀이 부적절한 게임 수정 시 오류 발생")
    @Test
    void givenGameWithInvalidTeam_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gameService.updateGame(eq(1L), any())).thenThrow(new WebApplicationException(ErrorCode.TEAM_NOT_FOUND));

        mvc.perform(patch("/v1/web/games/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameUpdateRequest("baseball", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.TEAM_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("장소가 부적절한 게임 수정 시 오류 발생")
    @Test
    void givenGameWithInvalidPlace_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(gameService.updateGame(eq(1L), any())).thenThrow(new WebApplicationException(ErrorCode.PLACE_NOT_FOUND));

        mvc.perform(patch("/v1/web/games/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameUpdateRequest("baseball", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.PLACE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 회원이 게임 수정 시 오류 발생")
    @Test
    void givenNonAdminMember_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        when(gameService.updateGame(eq(1L), any())).thenReturn(mock(GameDTO.class));

        mvc.perform(patch("/v1/web/games/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameUpdateRequest("baseball", "title", "home", "away", "place", new Timestamp(System.currentTimeMillis())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("게임 삭제 정상 동작")
    @Test
    void givenGame_whenDelete_thenDeletesGame() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doNothing().when(gameService).deleteGame(eq(1L));

        mvc.perform(patch("/v1/web/games/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 게임 삭제 시 오류 발생")
    @Test
    void givenNonExistentGame_whenDelete_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.GAME_NOT_FOUND)).when(gameService).deleteGame(eq(1L));

        mvc.perform(patch("/v1/web/games/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GAME_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 회원이 게임 수정 시 오류 발생")
    @Test
    void givenNonAdminMember_whenDelete_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        doNothing().when(gameService).deleteGame(eq(1L));

        mvc.perform(patch("/v1/web/games/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("게임 복구 정상 동작")
    @Test
    void givenGame_whenRestore_thenRestoresGame() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doNothing().when(gameService).restoreGame(eq(1L));

        mvc.perform(patch("/v1/web/games/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("존재하는 게임 복구 시 오류 발생")
    @Test
    void givenExistentGame_whenRestore_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.GAME_ALREADY_EXISTS)).when(gameService).restoreGame(eq(1L));

        mvc.perform(patch("/v1/web/games/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GAME_ALREADY_EXISTS.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 회원이 게임 복구 시 오류 발생")
    @Test
    void givenNonAdminMember_whenRestore_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        doNothing().when(gameService).restoreGame(eq(1L));

        mvc.perform(patch("/v1/web/games/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

}
