package com.ticketcheater.webservice.controller.response.game;

import com.ticketcheater.webservice.dto.GameDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GameReadByHomeResponse {
    private Long teamId;
    private List<GameDTO> games;

    public static GameReadByHomeResponse from(Long teamId, List<GameDTO> games) {
        GameReadByHomeResponse response = new GameReadByHomeResponse();
        response.setTeamId(teamId);
        response.setGames(games);
        return response;
    }
}