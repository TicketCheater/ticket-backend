package com.ticketcheater.webservice.controller.response.ticket;

import com.ticketcheater.webservice.dto.TicketDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketReserveResponse {
    private Long ticketId;
    private Long memberId;
    private Boolean isReserved;

    public static TicketReserveResponse from(TicketDTO dto) {
        return new TicketReserveResponse(dto.getId(), dto.getMemberId(), dto.isReserved());
    }
}
