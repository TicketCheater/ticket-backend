package com.ticketcheater.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.webservice.controller.request.place.PlaceCreateRequest;
import com.ticketcheater.webservice.controller.request.place.PlaceUpdateRequest;
import com.ticketcheater.webservice.dto.PlaceDTO;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.MemberService;
import com.ticketcheater.webservice.service.PlaceService;
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

@DisplayName("Controller - 장소")
@SpringBootTest
@AutoConfigureMockMvc
class PlaceControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MemberService memberService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    PlaceService placeService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("장소 생성 정상 동작")
    @Test
    void givenPlace_whenCreate_thenCreatesPlace() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(placeService.createPlace(any())).thenReturn(mock(PlaceDTO.class));

        mvc.perform(post("/v1/web/places/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PlaceCreateRequest("장소이름"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("관리자가 아닌 회원이 장소 생성 시 오류 발생")
    @Test
    void givenNonAdminMember_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        when(placeService.createPlace(any())).thenReturn(mock(PlaceDTO.class));

        mvc.perform(post("/v1/web/places/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PlaceCreateRequest("장소이름"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("부적절한 이름으로 장소 생성 시 오류 발생")
    @Test
    void givenPlaceWithInvalidName_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(placeService.createPlace(any())).thenThrow(new WebApplicationException(ErrorCode.INVALID_PLACE));

        mvc.perform(post("/v1/web/places/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PlaceCreateRequest("invalid"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PLACE.getStatus().value()));
    }

    @DisplayName("이미 존재하는 장소 이름으로 생성 시 오류 발생")
    @Test
    void givenExistentPlace_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(placeService.createPlace(any())).thenThrow(new WebApplicationException(ErrorCode.PLACE_ALREADY_EXISTS));

        mvc.perform(post("/v1/web/places/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PlaceCreateRequest("장소이름"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.PLACE_ALREADY_EXISTS.getStatus().value()));
    }

    @DisplayName("장소 수정 정상 동작")
    @Test
    void givenPlace_whenUpdate_thenUpdatesPlace() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(placeService.updatePlace(eq(1L), any())).thenReturn(mock(PlaceDTO.class));

        mvc.perform(patch("/v1/web/places/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PlaceUpdateRequest("새장소이름"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 장소 수정 시 오류 발생")
    @Test
    void givenNonExistentPlace_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(placeService.updatePlace(eq(1L), any())).thenThrow(new WebApplicationException(ErrorCode.PLACE_NOT_FOUND));

        mvc.perform(patch("/v1/web/places/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PlaceUpdateRequest("새장소이름"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.PLACE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("부적절한 이름으로 장소 수정 시 오류 발생")
    @Test
    void givenPlaceWithInvalidName_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        when(placeService.updatePlace(eq(1L), any())).thenThrow(new WebApplicationException(ErrorCode.INVALID_PLACE));

        mvc.perform(patch("/v1/web/places/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PlaceUpdateRequest("invalid"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PLACE.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 회원이 장소 수정 시 오류 발생")
    @Test
    void givenNonAdminMember_whenUpdate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        when(placeService.updatePlace(eq(1L), any())).thenReturn(mock(PlaceDTO.class));

        mvc.perform(patch("/v1/web/places/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PlaceUpdateRequest("새장소이름"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("장소 삭제 정상 동작")
    @Test
    void givenPlace_whenDelete_thenDeletesPlace() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doNothing().when(placeService).deletePlace(eq(1L));

        mvc.perform(patch("/v1/web/places/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 장소 삭제 시 오류 발생")
    @Test
    void givenNonExistentPlace_whenDelete_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.PLACE_NOT_FOUND)).when(placeService).deletePlace(eq(1L));

        mvc.perform(patch("/v1/web/places/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.PLACE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 회원이 장소 삭제 시 오류 발생")
    @Test
    void givenNonAdminMember_whenDelete_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        doNothing().when(placeService).deletePlace(eq(1L));

        mvc.perform(patch("/v1/web/places/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("장소 복구 정상 동작")
    @Test
    void givenPlace_whenRestore_thenRestoresPlace() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doNothing().when(placeService).restorePlace(eq(1L));

        mvc.perform(patch("/v1/web/places/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 장소 복구 시 오류 발생")
    @Test
    void givenNonExistentPlace_whenRestore_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.PLACE_NOT_FOUND)).when(placeService).restorePlace(eq(1L));

        mvc.perform(patch("/v1/web/places/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.PLACE_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 회원이 장소 복구 시 오류 발생")
    @Test
    void givenNonAdminMember_whenRestore_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        doNothing().when(placeService).restorePlace(eq(1L));

        mvc.perform(patch("/v1/web/places/restore/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

}
