package com.ticketcheater.webservice.controller.response.game;

import com.ticketcheater.webservice.dto.GameDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameCreateResponse {
    private Long id;
    private String title;

    public static GameCreateResponse from(GameDTO game) {
        return new GameCreateResponse(game.getId(), game.getTitle());
    }
}
