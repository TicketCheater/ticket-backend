package com.ticketcheater.webservice.dto;

import com.ticketcheater.webservice.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class TicketDTO {

    private Long id;
    private Long gameId;
    private Long memberId;
    private Long gradeId;
    private boolean isReserved;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static TicketDTO toDTO(Ticket ticket) {
        return new TicketDTO(
                ticket.getId(),
                ticket.getGame().getId(),
                ticket.getMember() != null ? ticket.getMember().getId() : null,
                ticket.getGrade().getId(),
                ticket.isReserved(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getDeletedAt()
        );
    }
}
