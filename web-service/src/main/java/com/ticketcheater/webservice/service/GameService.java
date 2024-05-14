package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.GameDTO;
import com.ticketcheater.webservice.entity.Game;
import com.ticketcheater.webservice.entity.GameType;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    @Transactional
    public GameDTO createGame(GameDTO dto) {
        Game game = gameRepository.save(
                Game.of(
                        GameType.fromString(dto.getType()),
                        dto.getTitle(),
                        dto.getHome(),
                        dto.getAway(),
                        dto.getPlace(),
                        dto.getStartedAt()
                )
        );
        return GameDTO.toDTO(game);
    }

    @Transactional
    public GameDTO updateGame(Long gameId, GameDTO dto) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.GAME_NOT_FOUND));

        game.setTitle(dto.getTitle());
        game.setHome(dto.getHome());
        game.setAway(dto.getAway());
        game.setPlace(dto.getPlace());
        game.setStartedAt(dto.getStartedAt());

        gameRepository.saveAndFlush(game);

        return GameDTO.toDTO(game);
    }

    @Transactional
    public void deleteGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.GAME_NOT_FOUND));
        game.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        gameRepository.saveAndFlush(game);
    }

}
