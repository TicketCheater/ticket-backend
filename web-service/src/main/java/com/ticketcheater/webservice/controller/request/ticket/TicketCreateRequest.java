package com.ticketcheater.webservice.controller.request.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateRequest {
    private Long gameId;
    private Long gradeId;
    private int quantity;
    private int price;
}
