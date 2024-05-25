package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.entity.*;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.fixture.*;
import com.ticketcheater.webservice.repository.GameRepository;
import com.ticketcheater.webservice.repository.GradeRepository;
import com.ticketcheater.webservice.repository.MemberRepository;
import com.ticketcheater.webservice.repository.TicketRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Business Logic - 티켓")
@SpringBootTest
class TicketServiceTest {

    @Autowired
    TicketService sut;

    @MockBean
    GameRepository gameRepository;

    @MockBean
    GradeRepository gradeRepository;

    @MockBean
    JdbcTemplate jdbcTemplate;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private TicketRepository ticketRepository;

    @DisplayName("티켓 생성 정상 동작")
    @Test
    void givenTicketInfo_whenCreate_thenCreatesTicket() {
        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = 20000;
        int price = 18000;

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        assertDoesNotThrow(() -> sut.createTickets(gameId, gradeId, quantity, price));

        verify(jdbcTemplate, times((quantity+999)/1000))
                .batchUpdate(
                        eq("INSERT INTO ticket (game_id, member_id, grade_id, payment_id, price, is_reserved) VALUES (?, null, ?, null, ?, ?)"),
                        anyList()
                );
    }

    @DisplayName("부적절한 티켓 개수로 생성 시 정상 동작")
    @Test
    void givenInvalidQuantity_whenCreate_thenThrowsError() {
        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = -1;
        int price = 18000;

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createTickets(gameId, gradeId, quantity, price)
        );

        assertEquals(ErrorCode.INVALID_TICKET_QUANTITY, exception.getCode());
    }

    @DisplayName("부적절한 가격으로 생성 시 정상 동작")
    @Test
    void givenInvalidPrice_whenCreate_thenThrowsError() {
        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = 20000;
        int price = -1;

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createTickets(gameId, gradeId, quantity, price)
        );

        assertEquals(ErrorCode.INVALID_TICKET_PRICE, exception.getCode());
    }

    @DisplayName("없는 게임의 티켓 생성 시 오류 발생")
    @Test
    void givenInvalidGame_whenCreate_thenThrowsError() {
        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = 20000;
        int price = 18000;

        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.empty());
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createTickets(gameId, gradeId, quantity, price)
        );

        assertEquals(ErrorCode.GAME_NOT_FOUND, exception.getCode());
    }

    @DisplayName("없는 등급의 티켓 생성 시 오류 발생")
    @Test
    void givenInvalidGrade_whenCreate_thenThrowsError() {
        Long gameId = 1L;
        Long gradeId = 1L;
        int quantity = 20000;
        int price = 18000;

        Game game = GameFixture.get(gameId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createTickets(gameId, gradeId, quantity, price)
        );

        assertEquals(ErrorCode.GRADE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("티켓 예약 정상 동작")
    @Test
    void givenTicketAndMember_whenReserve_thenReservesTicket() {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;
        String memberName = "name";

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Ticket ticket = TicketFixture.get(ticketId, game, grade);
        Member member = MemberFixture.get(memberName);

        ticket.setReserved(false);

        when(ticketRepository.findByIdAndDeletedAtIsNullWithLock(ticketId)).thenReturn(Optional.of(ticket));
        when(memberRepository.findByNameAndDeletedAtIsNull(memberName)).thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> sut.reserveTicket(ticketId, memberName));
    }

    @DisplayName("없는 회원이 티켓 예약 시 오류 발생")
    @Test
    void givenNonExistentMember_whenReserve_thenThrowsError() {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;
        String memberName = "name";

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Ticket ticket = TicketFixture.get(ticketId, game, grade);
        ticket.setReserved(false);

        when(ticketRepository.findByIdAndDeletedAtIsNullWithLock(ticketId)).thenReturn(Optional.of(ticket));
        when(memberRepository.findByNameAndDeletedAtIsNull(memberName)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.reserveTicket(ticketId, memberName)
        );

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getCode());
    }

    @DisplayName("이미 예약된 티켓 예약 시 오류 발생")
    @Test
    void givenAlreadyBookedTicket_whenReserve_thenThrowsError() {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;
        String memberName = "name";

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Ticket ticket = TicketFixture.get(ticketId, game, grade);
        Member member = MemberFixture.get(memberName);

        ticket.setReserved(true);

        when(ticketRepository.findByIdAndDeletedAtIsNullWithLock(ticketId)).thenReturn(Optional.of(ticket));
        when(memberRepository.findByNameAndDeletedAtIsNull(memberName)).thenReturn(Optional.of(member));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.reserveTicket(ticketId, memberName)
        );

        assertEquals(ErrorCode.TICKET_ALREADY_BOOKED, exception.getCode());
    }

    @DisplayName("티켓 예약 동시성 테스트")
    @Test
    void givenConcurrentRequests_whenReserveTicket_thenHandleCorrectly() throws InterruptedException {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;
        String memberName = "name";

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Ticket ticket = TicketFixture.get(ticketId, game, grade);
        Member member = MemberFixture.get(memberName);

        when(ticketRepository.findByIdAndDeletedAtIsNullWithLock(ticketId)).thenReturn(Optional.of(ticket));
        when(memberRepository.findByNameAndDeletedAtIsNull(memberName)).thenReturn(Optional.of(member));

        ticket.setReserved(false);

        Runnable reserveTask = () -> {
            try {
                sut.reserveTicket(ticketId, memberName);
            } catch (WebApplicationException e) {
                if (e.getCode() != ErrorCode.TICKET_ALREADY_BOOKED) {
                    fail("Unexpected exception: " + e.getMessage());
                }
            }
        };

        runConcurrentTest(reserveTask);

        verify(ticketRepository, times(1)).saveAndFlush(ticket);
    }

    @DisplayName("결제 생성 정상 동작")
    @Test
    void givenPayment_whenCreates_thenCreatesPayment() {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;
        String memberName = "name";
        int price = 20000;
        String method = "card";

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Ticket ticket = TicketFixture.get(ticketId, game, grade, price);
        Member member = MemberFixture.get(memberName);

        when(ticketRepository.findByIdAndDeletedAtIsNull(ticketId)).thenReturn(Optional.of(ticket));
        when(memberRepository.findByNameAndDeletedAtIsNull(memberName)).thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> sut.createPayment(ticketId, memberName, method, price));
    }

    @DisplayName("없는 티켓 결제 생성 시 오류 발생")
    @Test
    void givenNonExistentTicket_whenCreates_thenThrowsError() {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;
        String memberName = "name";
        int price = 20000;
        String method = "card";

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Member member = MemberFixture.get(memberName);

        when(ticketRepository.findByIdAndDeletedAtIsNull(ticketId)).thenReturn(Optional.empty());
        when(memberRepository.findByNameAndDeletedAtIsNull(memberName)).thenReturn(Optional.of(member));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createPayment(ticketId, memberName, method, price)
        );

        assertEquals(ErrorCode.TICKET_NOT_FOUND, exception.getCode());
    }

    @DisplayName("부적절한 금액으로 결제 생성 시 오류 발생")
    @Test
    void givenInvalidAmount_whenCreates_thenThrowsError() {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;
        String memberName = "name";
        int price = 20000;
        String method = "card";

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Ticket ticket = TicketFixture.get(ticketId, game, grade, price);
        Member member = MemberFixture.get(memberName);

        when(ticketRepository.findByIdAndDeletedAtIsNull(ticketId)).thenReturn(Optional.of(ticket));
        when(memberRepository.findByNameAndDeletedAtIsNull(memberName)).thenReturn(Optional.of(member));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createPayment(ticketId, memberName, method, price-1)
        );

        assertEquals(ErrorCode.INVALID_TICKET_PRICE, exception.getCode());
    }

    @DisplayName("없는 회원 결제 생성 시 오류 발생")
    @Test
    void givenNonExistentMember_whenCreates_thenThrowsError() {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;
        String memberName = "name";
        int price = 20000;
        String method = "card";

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Ticket ticket = TicketFixture.get(ticketId, game, grade, price);

        when(ticketRepository.findByIdAndDeletedAtIsNull(ticketId)).thenReturn(Optional.of(ticket));
        when(memberRepository.findByNameAndDeletedAtIsNull(memberName)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createPayment(ticketId, memberName, method, price)
        );

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getCode());
    }

    @DisplayName("부적절한 결제 수단으로 결제 생성 시 오류 발생")
    @Test
    void givenInvalidPaymentMethod_whenCreates_thenThrowsError() {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;
        String memberName = "name";
        int price = 20000;
        String method = "invalid";

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Ticket ticket = TicketFixture.get(ticketId, game, grade, price);
        Member member = MemberFixture.get(memberName);

        when(ticketRepository.findByIdAndDeletedAtIsNull(ticketId)).thenReturn(Optional.of(ticket));
        when(memberRepository.findByNameAndDeletedAtIsNull(memberName)).thenReturn(Optional.of(member));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createPayment(ticketId, memberName, method, price)
        );

        assertEquals(ErrorCode.PAYMENT_METHOD_NOT_FOUND, exception.getCode());
    }

    @DisplayName("티켓 취소 정상 동작")
    @Test
    void givenTicket_whenCancels_thenCancelsReservation() {
        Long ticketId = 1L;
        Long gameId = 1L;
        Long gradeId = 1L;

        Game game = GameFixture.get(gameId);
        Grade grade = GradeFixture.get(gradeId);

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        Ticket ticket = TicketFixture.get(ticketId, game, grade);

        when(ticketRepository.findByIdAndDeletedAtIsNull(ticketId)).thenReturn(Optional.of(ticket));

        assertDoesNotThrow(() -> sut.cancelReservation(ticketId));
    }

    @DisplayName("없는 티켓 취소 시 오류 발생")
    @Test
    void givenNonExistentTicket_whenCancels_thenThrowsError() {
        Long ticketId = 1L;

        when(ticketRepository.findByIdAndDeletedAtIsNull(ticketId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.cancelReservation(ticketId)
        );

        assertEquals(ErrorCode.TICKET_NOT_FOUND, exception.getCode());
    }

    // 동시성 테스트 설정
    private void runConcurrentTest(Runnable task) throws InterruptedException {
        final int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i=0; i<threadCount; i++) {
            executorService.execute(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }

}
