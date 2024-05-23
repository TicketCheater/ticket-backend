package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.GameDTO;
import com.ticketcheater.webservice.entity.Game;
import com.ticketcheater.webservice.entity.GameType;
import com.ticketcheater.webservice.entity.Place;
import com.ticketcheater.webservice.entity.Team;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.fixture.GameDTOFixture;
import com.ticketcheater.webservice.fixture.PlaceFixture;
import com.ticketcheater.webservice.fixture.TeamFixture;
import com.ticketcheater.webservice.repository.GameRepository;
import com.ticketcheater.webservice.repository.PlaceRepository;
import com.ticketcheater.webservice.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Business Logic - 게임")
@SpringBootTest
class GameServiceTest {

    @Autowired
    GameService sut;

    @MockBean
    GameRepository gameRepository;

    @MockBean
    TeamRepository teamRepository;

    @MockBean
    PlaceRepository placeRepository;

    @DisplayName("게임 생성 정상 동작")
    @Test
    void givenGame_whenCreate_thenCreatesGame() {
        GameDTO dto = GameDTOFixture.get("baseball");
        Team home = TeamFixture.get(dto.getHomeId());
        Team away = TeamFixture.get(dto.getAwayId());
        Place place = PlaceFixture.get(dto.getPlaceId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                home,
                away,
                place,
                dto.getStartedAt()
        );

        when(gameRepository.save(any())).thenReturn(game);
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.of(home));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.of(away));
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.of(place));

        assertDoesNotThrow(() -> sut.createGame(dto));
    }

    @DisplayName("타입이 부적절한 게임 생성 시 오류 발생")
    @Test
    void givenGameWithInvalidType_whenCreate_thenThrowsError() {
        GameDTO dto = GameDTOFixture.get("wrong");

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createGame(dto)
        );

        assertEquals(ErrorCode.GAME_TYPE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("홈 팀이 부적절한 게임 생성 시 오류 발생")
    @Test
    void givenGameWithInvalidHome_whenCreate_thenThrowsError() {
        GameDTO dto = GameDTOFixture.get("baseball");
        Team away = TeamFixture.get(dto.getAwayId());
        Place place = PlaceFixture.get(dto.getPlaceId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                null,
                away,
                place,
                dto.getStartedAt()
        );

        when(gameRepository.save(any())).thenReturn(game);
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.empty());
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.of(away));
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.of(place));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createGame(dto)
        );

        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getCode());
    }

    @DisplayName("원정 팀이 부적절한 게임 생성 시 오류 발생")
    @Test
    void givenGameWithInvalidAway_whenCreate_thenThrowsError() {
        GameDTO dto = GameDTOFixture.get("baseball");
        Team home = TeamFixture.get(dto.getHomeId());
        Place place = PlaceFixture.get(dto.getPlaceId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                home,
                null,
                place,
                dto.getStartedAt()
        );

        when(gameRepository.save(any())).thenReturn(game);
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.of(home));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.empty());
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.of(place));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createGame(dto)
        );

        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getCode());
    }

    @DisplayName("장소가 부적절한 게임 생성 시 오류 발생")
    @Test
    void givenGameWithInvalidPlace_whenCreate_thenThrowsError() {
        GameDTO dto = GameDTOFixture.get("baseball");
        Team home = TeamFixture.get(dto.getHomeId());
        Team away = TeamFixture.get(dto.getAwayId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                home,
                away,
                null,
                dto.getStartedAt()
        );

        when(gameRepository.save(any())).thenReturn(game);
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.of(home));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.of(away));
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createGame(dto)
        );

        assertEquals(ErrorCode.PLACE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("게임 조회 정상 동작")
    @Test
    void givenTeamId_whenRead_thenReadsTeam() {
        Long teamId = 1L;
        Team home = TeamFixture.get("팀이름");

        when(teamRepository.findByIdAndDeletedAtIsNull(teamId)).thenReturn(Optional.of(home));
        when(gameRepository.findByHomeAndDeletedAtIsNull(home)).thenReturn(List.of());

        assertDoesNotThrow(() -> sut.getGamesByHome(teamId));
    }

    @DisplayName("없는 팀의 게임 조회 시 오류 발생")
    @Test
    void givenNonExistentTeam_whenRead_thenThrowsError() {
        Long teamId = 1L;

        when(teamRepository.findByIdAndDeletedAtIsNull(teamId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.getGamesByHome(teamId)
        );

        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getCode());
    }

    @DisplayName("게임 수정 정상 동작")
    @Test
    void givenGame_whenUpdate_thenUpdatesGame() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");
        Team home = TeamFixture.get(dto.getHomeId());
        Team away = TeamFixture.get(dto.getAwayId());
        Place place = PlaceFixture.get(dto.getPlaceId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                home,
                away,
                place,
                dto.getStartedAt()
        );

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.of(home));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.of(away));
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.of(place));
        when(gameRepository.saveAndFlush(any())).thenReturn(game);

        assertDoesNotThrow(() -> sut.updateGame(gameId, dto));
    }

    @DisplayName("없는 게임 수정 시 오류 발생")
    @Test
    void givenNonExistentGame_whenUpdate_thenThrowsError() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.updateGame(gameId, dto)
        );

        assertEquals(ErrorCode.GAME_NOT_FOUND, exception.getCode());
    }

    @DisplayName("홈 팀이 부적절한 게임 수정 시 오류 발생")
    @Test
    void givenGameWithInvalidHome_whenUpdate_thenThrowsError() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");
        Team away = TeamFixture.get(dto.getAwayId());
        Place place = PlaceFixture.get(dto.getPlaceId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                null,
                away,
                place,
                dto.getStartedAt()
        );

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.empty());
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.of(away));
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.of(place));
        when(gameRepository.saveAndFlush(any())).thenReturn(game);

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.updateGame(gameId, dto)
        );

        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getCode());
    }

    @DisplayName("원정 팀이 부적절한 게임 수정 시 오류 발생")
    @Test
    void givenGameWithInvalidAway_whenUpdate_thenThrowsError() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");
        Team home = TeamFixture.get(dto.getHomeId());
        Place place = PlaceFixture.get(dto.getPlaceId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                home,
                null,
                place,
                dto.getStartedAt()
        );

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.of(home));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.empty());
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.of(place));
        when(gameRepository.saveAndFlush(any())).thenReturn(game);

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.updateGame(gameId, dto)
        );

        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getCode());
    }

    @DisplayName("장소가 부적절한 게임 수정 시 오류 발생")
    @Test
    void givenGameWithInvalidPlace_whenUpdate_thenThrowsError() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");
        Team home = TeamFixture.get(dto.getHomeId());
        Team away = TeamFixture.get(dto.getAwayId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                home,
                away,
                null,
                dto.getStartedAt()
        );

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.of(home));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.of(away));
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.empty());
        when(gameRepository.saveAndFlush(any())).thenReturn(game);

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.updateGame(gameId, dto)
        );

        assertEquals(ErrorCode.PLACE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("게임 삭제 정상 동작")
    @Test
    void givenGame_whenDelete_thenDeletesGame() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");
        Team home = TeamFixture.get(dto.getHomeId());
        Team away = TeamFixture.get(dto.getAwayId());
        Place place = PlaceFixture.get(dto.getPlaceId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                home,
                away,
                place,
                dto.getStartedAt()
        );

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.of(game));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.of(home));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.of(away));
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.of(place));
        when(gameRepository.saveAndFlush(any())).thenReturn(game);

        assertDoesNotThrow(() -> sut.deleteGame(gameId));
    }

    @DisplayName("없는 게임 삭제 시 오류 발생")
    @Test
    void givenNonExistentGame_whenDelete_thenThrowsError() {
        Long gameId = 1L;

        when(gameRepository.findByIdAndDeletedAtIsNull(gameId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.deleteGame(gameId)
        );

        assertEquals(ErrorCode.GAME_NOT_FOUND, exception.getCode());
    }

    @DisplayName("게임 복구 정상 동작")
    @Test
    void givenGame_whenRestore_thenRestoresGame() {
        Long gameId = 1L;
        GameDTO dto = GameDTOFixture.get("baseball");
        Team home = TeamFixture.get(dto.getHomeId());
        Team away = TeamFixture.get(dto.getAwayId());
        Place place = PlaceFixture.get(dto.getPlaceId());

        Game game = Game.of(
                GameType.fromString(dto.getType()),
                dto.getTitle(),
                home,
                away,
                place,
                dto.getStartedAt()
        );

        when(gameRepository.findByIdAndDeletedAtIsNotNull(gameId)).thenReturn(Optional.of(game));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getHomeId())).thenReturn(Optional.of(home));
        when(teamRepository.findByIdAndDeletedAtIsNull(dto.getAwayId())).thenReturn(Optional.of(away));
        when(placeRepository.findByIdAndDeletedAtIsNull(dto.getPlaceId())).thenReturn(Optional.of(place));
        when(gameRepository.saveAndFlush(any())).thenReturn(game);

        assertDoesNotThrow(() -> sut.restoreGame(gameId));
    }

    @DisplayName("존재하는 게임 삭제 시 오류 발생")
    @Test
    void givenExistentGame_whenRestore_thenThrowsError() {
        Long gameId = 1L;

        when(gameRepository.findByIdAndDeletedAtIsNotNull(gameId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.restoreGame(gameId)
        );

        assertEquals(ErrorCode.GAME_ALREADY_EXISTS, exception.getCode());
    }

}
