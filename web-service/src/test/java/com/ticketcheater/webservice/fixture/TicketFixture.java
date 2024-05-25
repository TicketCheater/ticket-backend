package com.ticketcheater.webservice.fixture;

import com.ticketcheater.webservice.entity.Game;
import com.ticketcheater.webservice.entity.Grade;
import com.ticketcheater.webservice.entity.Ticket;

public class TicketFixture {

    public static Ticket get(Long id, Game game, Grade grade) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setGame(game);
        ticket.setGrade(grade);
        return ticket;
    }

    public static Ticket get(Long id, Game game, Grade grade, int price) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setGame(game);
        ticket.setGrade(grade);
        ticket.setPrice(price);
        return ticket;
    }

}
