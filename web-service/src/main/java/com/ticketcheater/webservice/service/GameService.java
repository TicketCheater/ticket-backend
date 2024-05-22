package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.GameDTO;
import com.ticketcheater.webservice.entity.Game;
import com.ticketcheater.webservice.entity.GameType;
import com.ticketcheater.webservice.entity.Place;
import com.ticketcheater.webservice.entity.Team;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.GameRepository;
import com.ticketcheater.webservice.repository.PlaceRepository;
import com.ticketcheater.webservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public GameDTO createGame(GameDTO dto) {
        Game game = gameRepository.save(
                Game.of(
                        GameType.fromString(dto.getType()),
                        dto.getTitle(),
                        findTeamById(dto.getHomeId()),
                        findTeamById(dto.getAwayId()),
                        findPlaceById(dto.getPlaceId()),
                        dto.getStartedAt()
                )
        );

        return GameDTO.toDTO(game);
    }

    @Transactional(readOnly = true)
    public List<GameDTO> getGamesByHome(Long teamId) {
        Team team = teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.TEAM_NOT_FOUND));

        List<Game> games = gameRepository.findByHomeAndDeletedAtIsNull(team);

        return games.stream()
                .map(GameDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GameDTO updateGame(Long gameId, GameDTO dto) {
        Game game = gameRepository.findByIdAndDeletedAtIsNull(gameId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.GAME_NOT_FOUND));

        game.setTitle(dto.getTitle());
        game.setHome(findTeamById(dto.getHomeId()));
        game.setAway(findTeamById(dto.getAwayId()));
        game.setPlace(findPlaceById(dto.getPlaceId()));
        game.setStartedAt(dto.getStartedAt());

        gameRepository.saveAndFlush(game);

        return GameDTO.toDTO(game);
    }

    @Transactional
    public void deleteGame(Long gameId) {
        Game game = gameRepository.findByIdAndDeletedAtIsNull(gameId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.GAME_NOT_FOUND)
        );

        game.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        gameRepository.saveAndFlush(game);
    }

    @Transactional
    public void restoreGame(Long gameId) {
        Game game = gameRepository.findByIdAndDeletedAtIsNotNull(gameId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.GAME_ALREADY_EXISTS)
        );

        game.setDeletedAt(null);

        gameRepository.saveAndFlush(game);
    }

    private Team findTeamById(Long teamId) {
        return teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.TEAM_NOT_FOUND));
    }

    private Place findPlaceById(Long placeId) {
        return placeRepository.findByIdAndDeletedAtIsNull(placeId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.PLACE_NOT_FOUND));
    }

}
