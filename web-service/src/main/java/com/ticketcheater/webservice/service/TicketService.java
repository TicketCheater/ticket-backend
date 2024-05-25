package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.PaymentDTO;
import com.ticketcheater.webservice.dto.TicketDTO;
import com.ticketcheater.webservice.entity.Member;
import com.ticketcheater.webservice.entity.Payment;
import com.ticketcheater.webservice.entity.PaymentMethod;
import com.ticketcheater.webservice.entity.Ticket;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final GameRepository gameRepository;
    private final GradeRepository gradeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void createTickets(Long gameId, Long gradeId, int quantity, int price) {
        if(quantity <= 0) {
            throw new WebApplicationException(ErrorCode.INVALID_TICKET_QUANTITY, String.format("ticket quantity cannot be %d", quantity));
        }
        if(price <= 0) {
            throw new WebApplicationException(ErrorCode.INVALID_TICKET_PRICE, String.format("ticket price cannot be %d", price));
        }

        findGameById(gameId);
        findGradeById(gradeId);

        int batchSize = 1000;
        List<Object[]> batchArgs = new ArrayList<>();

        for(int i=0; i<quantity; i++) {
            batchArgs.add(new Object[]{gameId, gradeId, price, false});
            if(batchArgs.size() == batchSize) {
                executeBatchInsert(batchArgs);
                batchArgs.clear();
            }
        }

        if(!batchArgs.isEmpty()) {
            executeBatchInsert(batchArgs);
        }

        log.info("create tickets method executed successfully: game id={}, grade id={}, quantity={}, price={}", gameId, gradeId, quantity, price);
    }

    @Transactional
    public TicketDTO reserveTicket(Long ticketId, String memberName) {
        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNullWithLock(ticketId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.TICKET_NOT_FOUND, String.format("ticket id %d not found", ticketId))
        );

        if(ticket.isReserved()) {
            throw new WebApplicationException(ErrorCode.TICKET_ALREADY_BOOKED, String.format("ticket id %d already booked", ticketId));
        }

        ticket.setMember(findMemberByName(memberName));
        ticket.setReserved(true);

        ticketRepository.saveAndFlush(ticket);

        log.info("reserve ticket method executed successfully: ticket id={}, member name={}", ticketId, memberName);

        return TicketDTO.toDTO(ticket);
    }

    @Transactional
    public PaymentDTO createPayment(Long ticketId, String memberName, String method, int amount) {
        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.TICKET_NOT_FOUND, String.format("ticket id %d not found", ticketId))
        );

        if(amount != ticket.getPrice()) {
            throw new WebApplicationException(ErrorCode.INVALID_TICKET_PRICE, String.format("ticket price cannot be %d", ticket.getPrice()));
        }

        Payment payment = paymentRepository.save(
                Payment.of(findMemberByName(memberName), PaymentMethod.fromString(method), amount)
        );

        ticket.setPayment(payment);

        ticketRepository.saveAndFlush(ticket);

        log.info("create payment method executed successfully: member name={}, method={}, amount={}", memberName, method, amount);

        return PaymentDTO.toDTO(payment);
    }

    @Transactional
    public void cancelReservation(Long ticketId) {
        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.TICKET_NOT_FOUND, String.format("ticket id %d not found", ticketId))
        );

        if(ticket.getPayment() != null) {
            ticket.getPayment().setDeletedAt(new Timestamp(System.currentTimeMillis()));
            paymentRepository.saveAndFlush(ticket.getPayment());
        }

        ticket.setMember(null);
        ticket.setReserved(false);

        ticketRepository.saveAndFlush(ticket);

        log.info("cancel reservation method executed successfully: ticket id={}", ticketId);
    }

    private void executeBatchInsert(List<Object[]> batchArgs) {
        String sql = "INSERT INTO ticket (game_id, member_id, grade_id, payment_id, price, is_reserved) VALUES (?, null, ?, null, ?, ?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void findGameById(Long gameId) {
        gameRepository.findByIdAndDeletedAtIsNull(gameId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.GAME_NOT_FOUND, String.format("game id %d not found", gameId))
        );
    }

    private void findGradeById(Long gradeId) {
        gradeRepository.findByIdAndDeletedAtIsNull(gradeId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.GRADE_NOT_FOUND, String.format("grade id %d not found", gradeId))
        );
    }

    private Member findMemberByName(String memberName) {
        return memberRepository.findByNameAndDeletedAtIsNull(memberName).orElseThrow(
                () -> new WebApplicationException(ErrorCode.MEMBER_NOT_FOUND, String.format("member name %s not found", memberName))
        );
    }

}
