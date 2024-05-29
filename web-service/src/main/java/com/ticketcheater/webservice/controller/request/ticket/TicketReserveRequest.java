package com.ticketcheater.webservice.controller.request.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketReserveRequest {
    private Long gameId;
    private Long ticketId;
}
