package com.ticketcheater.webservice.fixture;

import com.ticketcheater.webservice.entity.Game;

public class GameFixture {

    public static Game get(Long gameId) {
        Game game = new Game();
        game.setId(gameId);
        return game;
    }

}
