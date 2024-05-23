package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.entity.Game;
import com.ticketcheater.webservice.entity.Grade;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.fixture.GameFixture;
import com.ticketcheater.webservice.fixture.GradeFixture;
import com.ticketcheater.webservice.repository.GameRepository;
import com.ticketcheater.webservice.repository.GradeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

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
                        eq("INSERT INTO ticket (game_id, member_id, grade_id, price, is_reserved) VALUES (?, null, ?, ?, ?)"),
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

}
