package com.ticketcheater.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.webservice.controller.request.ticket.TicketCreateRequest;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.MemberService;
import com.ticketcheater.webservice.service.TicketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller - 티켓")
@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MemberService memberService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("티켓 생성 정상 동작")
    @Test
    void givenTicketInfo_whenCreate_thenCreatesTicket() throws Exception {
        String token = "dummy";
        String name = "name";

        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = 20000;
        int price = 18000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doNothing().when(ticketService).createTickets(eq(1L), eq(1L), eq(20000), eq(18000));

        mvc.perform(post("/v1/web/tickets/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TicketCreateRequest(gameId, gradeId, quantity, price))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("관리자가 아닌 회원이 티켓 생성 시 오류 발생")
    @Test
    void givenNonAdminMember_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = 20000;
        int price = 18000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TOKEN)).when(memberService).isAdmin(name);
        doNothing().when(ticketService).createTickets(eq(1L), eq(1L), eq(20000), eq(18000));

        mvc.perform(post("/v1/web/tickets/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TicketCreateRequest(gameId, gradeId, quantity, price))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("부적절한 티켓 개수로 생성 시 정상 동작")
    @Test
    void givenInvalidQuantity_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = -1;
        int price = 18000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TICKET_QUANTITY))
                .when(ticketService).createTickets(eq(1L), eq(1L), eq(-1), eq(18000));

        mvc.perform(post("/v1/web/tickets/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TicketCreateRequest(gameId, gradeId, quantity, price))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TICKET_QUANTITY.getStatus().value()));
    }

    @DisplayName("부적절한 가격으로 생성 시 정상 동작")
    @Test
    void givenInvalidPrice_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = 20000;
        int price = -1;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.INVALID_TICKET_PRICE))
                .when(ticketService).createTickets(eq(1L), eq(1L), eq(20000), eq(-1));

        mvc.perform(post("/v1/web/tickets/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TicketCreateRequest(gameId, gradeId, quantity, price))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TICKET_PRICE.getStatus().value()));
    }

    @DisplayName("없는 게임의 티켓 생성 시 오류 발생")
    @Test
    void givenInvalidGame_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = 20000;
        int price = 18000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.GAME_NOT_FOUND))
                .when(ticketService).createTickets(eq(1L), eq(1L), eq(20000), eq(18000));

        mvc.perform(post("/v1/web/tickets/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TicketCreateRequest(gameId, gradeId, quantity, price))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GAME_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("없는 등급의 티켓 생성 시 오류 발생")
    @Test
    void givenInvalidGrade_whenCreate_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = 20000;
        int price = 18000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        doNothing().when(memberService).isAdmin(name);
        doThrow(new WebApplicationException(ErrorCode.GRADE_NOT_FOUND))
                .when(ticketService).createTickets(eq(1L), eq(1L), eq(20000), eq(18000));

        mvc.perform(post("/v1/web/tickets/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new TicketCreateRequest(gameId, gradeId, quantity, price))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.GRADE_NOT_FOUND.getStatus().value()));
    }

}
