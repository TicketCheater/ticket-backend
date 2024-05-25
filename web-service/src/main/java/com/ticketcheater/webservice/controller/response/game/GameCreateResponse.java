package com.ticketcheater.webservice.controller.response.game;

import com.ticketcheater.webservice.dto.GameDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameCreateResponse {
    private Long gameId;
    private String gameTitle;

    public static GameCreateResponse from(GameDTO game) {
        return new GameCreateResponse(game.getId(), game.getTitle());
    }
}
