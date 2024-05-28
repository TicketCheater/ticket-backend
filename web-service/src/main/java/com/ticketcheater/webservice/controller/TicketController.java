package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.interceptor.RequireAdmin;
import com.ticketcheater.webservice.controller.request.ticket.PaymentRequest;
import com.ticketcheater.webservice.controller.request.ticket.TicketCreateRequest;
import com.ticketcheater.webservice.controller.response.Response;
import com.ticketcheater.webservice.controller.response.ticket.PaymentResponse;
import com.ticketcheater.webservice.controller.response.ticket.TicketReserveResponse;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/web/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final JwtTokenProvider jwtTokenProvider;

    @RequireAdmin
    @PostMapping("/create")
    public Response<Void> createTickets(@RequestBody TicketCreateRequest request) {
        ticketService.createTickets(request.getGameId(), request.getGradeId(), request.getQuantity(), request.getPrice());
        return Response.success();
    }

    @PostMapping("/reserve/{ticketId}")
    public Response<TicketReserveResponse> reserveTicket(
            @PathVariable Long ticketId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        return Response.success(TicketReserveResponse.from(ticketService.reserveTicket(
                ticketId, jwtTokenProvider.getName(header)
        )));
    }

    @PostMapping("/payment/{ticketId}")
    public Response<PaymentResponse> createPayment(
            @PathVariable Long ticketId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody PaymentRequest request
    ) {
        return Response.success(PaymentResponse.from(ticketService.createPayment(
                ticketId, jwtTokenProvider.getName(header), request.getMethod(), request.getAmount()
        )));
    }

    @PostMapping("/cancel/{ticketId}")
    public Response<Void> cancelReservation(@PathVariable Long ticketId) {
        ticketService.cancelReservation(ticketId);
        return Response.success();
    }

}
