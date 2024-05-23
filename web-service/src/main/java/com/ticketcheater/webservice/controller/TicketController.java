package com.ticketcheater.webservice.controller;

import com.ticketcheater.webservice.controller.request.ticket.TicketCreateRequest;
import com.ticketcheater.webservice.jwt.JwtTokenProvider;
import com.ticketcheater.webservice.service.MemberService;
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
    private final MemberService memberService;

    @PostMapping("/create")
    public void createTickets(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
            @RequestBody TicketCreateRequest request
    ) {
        memberService.isAdmin(jwtTokenProvider.getName(header));
        ticketService.createTickets(request.getGameId(), request.getGradeId(), request.getQuantity(), request.getPrice());
    }

}
