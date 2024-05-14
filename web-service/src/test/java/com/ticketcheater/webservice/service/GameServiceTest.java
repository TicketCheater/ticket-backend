package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.GameDTO;
import com.ticketcheater.webservice.entity.Game;
import com.ticketcheater.webservice.entity.GameType;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.fixture.GameDTOFixture;
import com.ticketcheater.webservice.repository.GameRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Business Logic - 게임")
@SpringBootTest
class GameServiceTest {

    @Autowired
    GameService sut;

    @MockBean
    GameRepository gameRepository;

    @DisplayName("게임 생성 정상 동작")
    @Test
    void givenGame_whenCreate_thenCreatesGame() {
        GameDTO dto = GameDTOFixture.get("baseball");
        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                dto.getHome(),
                dto.getAway(),
                dto.getPlace(),
                dto.getStartedAt()
        );

        when(gameRepository.save(any())).thenReturn(game);

        Assertions.assertDoesNotThrow(() -> sut.createGame(dto));
    }

    @DisplayName("타입이 부적절한 게임 생성 시 오류 발생")
    @Test
    void givenGameWithInvalidType_whenCreate_thenThrowsError() {
        GameDTO dto = GameDTOFixture.get("wrong");

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.createGame(dto)
        );

        Assertions.assertEquals(ErrorCode.TYPE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("게임 수정 정상 동작")
    @Test
    void givenGame_whenUpdate_thenUpdatesGame() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");
        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                dto.getHome(),
                dto.getAway(),
                dto.getPlace(),
                dto.getStartedAt()
        );

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepository.saveAndFlush(any())).thenReturn(game);

        Assertions.assertDoesNotThrow(() -> sut.updateGame(gameId, dto));
    }

    @DisplayName("없는 게임 수정 시 오류 발생")
    @Test
    void givenNonExistentGame_whenUpdate_thenThrowsError() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");

        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.updateGame(gameId, dto)
        );

        Assertions.assertEquals(ErrorCode.GAME_NOT_FOUND, exception.getCode());
    }

    @DisplayName("게임 삭제 정상 동작")
    @Test
    void givenGame_whenDelete_thenDeletesGame() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");
        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                dto.getHome(),
                dto.getAway(),
                dto.getPlace(),
                dto.getStartedAt()
        );

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepository.saveAndFlush(any())).thenReturn(game);

        Assertions.assertDoesNotThrow(() -> sut.deleteGame(gameId));
    }

    @DisplayName("없는 게임 삭제 시 오류 발생")
    @Test
    void givenNonExistentGame_whenDelete_thenThrowsError() {
        Long gameId = 1L;

        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.deleteGame(gameId)
        );

        Assertions.assertEquals(ErrorCode.GAME_NOT_FOUND, exception.getCode());
    }

}
