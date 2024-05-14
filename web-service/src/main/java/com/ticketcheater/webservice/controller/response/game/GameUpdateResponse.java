package com.ticketcheater.webservice.controller.response.game;

import com.ticketcheater.webservice.dto.GameDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameUpdateResponse {
    private Long id;
    private String title;

    public static GameUpdateResponse from(GameDTO game) {
        return new GameUpdateResponse(game.getId(), game.getTitle());
    }
}
