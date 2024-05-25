package com.ticketcheater.webservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.webservice.controller.request.ticket.PaymentRequest;
import com.ticketcheater.webservice.controller.request.ticket.TicketCreateRequest;
import com.ticketcheater.webservice.dto.PaymentDTO;
import com.ticketcheater.webservice.dto.TicketDTO;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    @DisplayName("티켓 예약 정상 동작")
    @Test
    void givenTicketAndMember_whenReserve_thenReservesTicket() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(ticketService.reserveTicket(eq(1L), eq(name))).thenReturn(mock(TicketDTO.class));

        mvc.perform(post("/v1/web/tickets/reserve/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 회원이 티켓 예약 시 오류 발생")
    @Test
    void givenNonExistentMember_whenReserve_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(ticketService.reserveTicket(eq(1L), eq(name))).thenThrow(new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        mvc.perform(post("/v1/web/tickets/reserve/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.MEMBER_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("이미 예약된 티켓 예약 시 오류 발생")
    @Test
    void givenAlreadyBookedTicket_whenReserve_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(ticketService.reserveTicket(eq(1L), eq(name))).thenThrow(new WebApplicationException(ErrorCode.TICKET_ALREADY_BOOKED));

        mvc.perform(post("/v1/web/tickets/reserve/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(ErrorCode.TICKET_ALREADY_BOOKED.getStatus().value()));
    }

    @DisplayName("결제 생성 정상 동작")
    @Test
    void givenPayment_whenCreates_thenCreatesPayment() throws Exception {
        String token = "dummy";
        String name = "name";
        String method = "card";
        int amount = 20000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(ticketService.createPayment(eq(1L), eq(name), eq(method), eq(amount))).thenReturn(mock(PaymentDTO.class));

        mvc.perform(post("/v1/web/tickets/payment/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PaymentRequest(method, amount))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 티켓 결제 생성 시 오류 발생")
    @Test
    void givenNonExistentTicket_whenCreates_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";
        String method = "card";
        int amount = 20000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(ticketService.createPayment(eq(1L), eq(name), eq(method), eq(amount))).thenThrow(new WebApplicationException(ErrorCode.TICKET_NOT_FOUND));

        mvc.perform(post("/v1/web/tickets/payment/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PaymentRequest(method, amount))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.TICKET_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("부적절한 금액으로 결제 생성 시 오류 발생")
    @Test
    void givenInvalidAmount_whenCreates_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";
        String method = "card";
        int amount = 20000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(ticketService.createPayment(eq(1L), eq(name), eq(method), eq(amount))).thenThrow(new WebApplicationException(ErrorCode.INVALID_TICKET_PRICE));

        mvc.perform(post("/v1/web/tickets/payment/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PaymentRequest(method, amount))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TICKET_PRICE.getStatus().value()));
    }

    @DisplayName("없는 회원 결제 생성 시 오류 발생")
    @Test
    void givenNonExistentMember_whenCreates_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";
        String method = "card";
        int amount = 20000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(ticketService.createPayment(eq(1L), eq(name), eq(method), eq(amount))).thenThrow(new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND));

        mvc.perform(post("/v1/web/tickets/payment/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PaymentRequest(method, amount))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.MEMBER_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("부적절한 결제 수단으로 결제 생성 시 오류 발생")
    @Test
    void givenInvalidPaymentMethod_whenCreates_thenThrowsError() throws Exception {
        String token = "dummy";
        String name = "name";
        String method = "invalid";
        int amount = 20000;

        when(jwtTokenProvider.getName(anyString())).thenReturn(name);
        when(ticketService.createPayment(eq(1L), eq(name), eq(method), eq(amount))).thenThrow(new WebApplicationException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        mvc.perform(post("/v1/web/tickets/payment/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PaymentRequest(method, amount))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.PAYMENT_METHOD_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("티켓 취소 정상 동작")
    @Test
    void givenTicket_whenCancels_thenCancelsReservation() throws Exception {
        doNothing().when(ticketService).cancelReservation(eq(1L));

        mvc.perform(post("/v1/web/tickets/cancel/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("없는 티켓 취소 시 오류 발생")
    @Test
    void givenNonExistentTicket_whenCancels_thenThrowsError() throws Exception {
        doThrow(new WebApplicationException(ErrorCode.TICKET_NOT_FOUND)).when(ticketService).cancelReservation(eq(1L));

        mvc.perform(post("/v1/web/tickets/cancel/1"))
                .andDo(print())
                .andExpect(status().is(ErrorCode.TICKET_NOT_FOUND.getStatus().value()));
    }

}
