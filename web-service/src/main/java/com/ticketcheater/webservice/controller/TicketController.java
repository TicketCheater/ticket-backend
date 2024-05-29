package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.controller.request.ticket.PaymentRequest;
import com.ticketcheater.webservice.controller.request.ticket.TicketCreateRequest;
import com.ticketcheater.webservice.controller.request.ticket.TicketReserveRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.ticket.PaymentResponse;
import com.ticketcheater.webservice.controller.response.ticket.TicketReserveResponse;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.interceptor.RequireAdmin;
import com.ticketcheater.webservice.service.TicketService;
import com.ticketcheater.webservice.token.JwtProvider;
import com.ticketcheater.webservice.token.TokenGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final JwtProvider jwtProvider;

    @RequireAdmin
    @PostMapping("/create")
    public Response<Void> createTickets(@RequestBody TicketCreateRequest request) {
        ticketService.createTickets(request.getGameId(), request.getGradeId(), request.getQuantity(), request.getPrice());
        return Response.success();
    }

    @PostMapping("/reserve")
    public Response<TicketReserveResponse> reserveTicket(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            HttpServletRequest httpRequest,
            @RequestBody TicketReserveRequest request
    ) {
        String memberName = jwtProvider.getName(header);
        String gameId = String.valueOf(request.getGameId());
        String tokenName = "member-queue-%s-token".formatted(gameId);

        String cookieToken = null;
        if(httpRequest.getCookies() != null) {
            for(Cookie cookie : httpRequest.getCookies()) {
                if(tokenName.equals(cookie.getName())) {
                    cookieToken = cookie.getValue();
                    break;
                }
            }
        }

        if(cookieToken == null || !TokenGenerator.validateToken(cookieToken, gameId, memberName)) {
            throw new WebApplicationException(ErrorCode.INVALID_TOKEN, "Invalid or missing token");
        }

        return Response.success(TicketReserveResponse.from(ticketService.reserveTicket(
                request.getTicketId(), memberName
        )));
    }

    @PostMapping("/payment/{ticketId}")
    public Response<PaymentResponse> createPayment(
            @PathVariable Long ticketId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody PaymentRequest request
    ) {
        return Response.success(PaymentResponse.from(ticketService.createPayment(
                ticketId, jwtProvider.getName(header), request.getMethod(), request.getAmount()
        )));
    }

    @PostMapping("/cancel/{ticketId}")
    public Response<Void> cancelReservation(@PathVariable Long ticketId) {
        ticketService.cancelReservation(ticketId);
        return Response.success();
    }

}
